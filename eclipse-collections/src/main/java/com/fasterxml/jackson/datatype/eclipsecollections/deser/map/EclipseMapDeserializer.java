package com.fasterxml.jackson.datatype.eclipsecollections.deser.map;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

/**
 * @author yawkat
 */
public abstract class EclipseMapDeserializer<T, I, K extends KeyHandler<K>, V extends ValueHandler<V>>
        extends JsonDeserializer<T> {
    private final K keyHandler;
    private final V valueHandler;

    public EclipseMapDeserializer(K keyHandler, V valueHandler) {
        this.keyHandler = keyHandler;
        this.valueHandler = valueHandler;
    }

    protected abstract EclipseMapDeserializer<T, ?, ?, ?> withResolved(K kh, V vh);

    protected abstract I createIntermediate();

    protected abstract void deserializeEntry(
            I target,
            K kh,
            V vh,
            DeserializationContext ctx,
            String key,
            JsonParser valueParser
    ) throws IOException;

    protected abstract T finish(I intermediate);

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
            throws JsonMappingException {
        K kc = keyHandler.createContextualKey(ctxt, property);
        V vc = valueHandler.createContextualValue(ctxt, property);
        //noinspection ObjectEquality
        if (kc == keyHandler && vc == valueHandler) {
            return this;
        } else {
            return withResolved(kc, vc);
        }
    }

    @Override
    public Object deserializeWithType(
            JsonParser p, DeserializationContext ctxt,
            TypeDeserializer typeDeserializer
    )
            throws IOException {
        // note: call "...FromObject" because expected output structure
        // for value is JSON Object (regardless of contortions used for type id)
        return typeDeserializer.deserializeTypedFromObject(p, ctxt);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException
    {
        // Ok: must point to START_OBJECT or FIELD_NAME
        JsonToken t = p.currentToken();
        if (t == JsonToken.START_OBJECT) { // If START_OBJECT, move to next; may also be END_OBJECT
            t = p.nextToken();
        }
        if (t != JsonToken.FIELD_NAME && t != JsonToken.END_OBJECT) {
            // !!! 16-Sep-2019, tatu: Should use full generic type, for error message,
            //   but would require more refactoring (to extend `StdDeserializer` f.ex)
            return (T) ctxt.handleUnexpectedToken(ctxt.constructType(handledType()), p);
        }

        I map = createIntermediate();
        for (; p.currentToken() == JsonToken.FIELD_NAME; p.nextToken()) {
            // Must point to field name now
            String fieldName = p.currentName();
            p.nextToken();
            deserializeEntry(map, keyHandler, valueHandler, ctxt, fieldName, p);
        }
        return finish(map);
    }
}
