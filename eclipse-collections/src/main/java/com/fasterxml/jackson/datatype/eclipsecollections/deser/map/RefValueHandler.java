package com.fasterxml.jackson.datatype.eclipsecollections.deser.map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.io.IOException;

/**
 * @author yawkat
 */
public class RefValueHandler implements ValueHandler<RefValueHandler> {
    private final JavaType _valueType;
    private final JsonDeserializer<?> _valueDeserializer;
    private final TypeDeserializer _typeDeserializerForValue;

    public RefValueHandler(
            JavaType valueType,
            JsonDeserializer<?> _valueDeserializer,
            TypeDeserializer _typeDeserializerForValue
    ) {
        if (valueType == null) { throw new IllegalArgumentException("valueType == null"); }

        this._valueType = valueType;
        this._valueDeserializer = _valueDeserializer;
        this._typeDeserializerForValue = _typeDeserializerForValue;
    }

    @Override
    public RefValueHandler createContextualValue(DeserializationContext ctxt, BeanProperty property)
            throws JsonMappingException {
        JsonDeserializer<?> deser;
        if (_valueDeserializer == null) {
            deser = ctxt.findContextualValueDeserializer(_valueType, property);
        } else {
            deser = _valueDeserializer;
        }
        TypeDeserializer typeDeser = _typeDeserializerForValue == null ?
                ctxt.getFactory().findTypeDeserializer(ctxt.getConfig(), _valueType) : _typeDeserializerForValue;
        if (typeDeser != null) {
            typeDeser = typeDeser.forProperty(property);
        }
        //noinspection ObjectEquality
        if (deser == this._valueDeserializer && typeDeser == this._typeDeserializerForValue) {
            return this;
        } else {
            return new RefValueHandler(_valueType, deser, typeDeser);
        }
    }

    public Object value(DeserializationContext ctx, JsonParser parser) throws IOException {
        if (parser.currentToken() == JsonToken.VALUE_NULL) {
            return _valueDeserializer.getNullValue(ctx);
        }
        if (_typeDeserializerForValue == null) {
            return _valueDeserializer.deserialize(parser, ctx);
        } else {
            return _valueDeserializer.deserializeWithType(parser, ctx, _typeDeserializerForValue);
        }
    }
}
