package com.fasterxml.jackson.datatype.guava.deser;

import java.io.IOException;
import java.util.Arrays;

import com.google.common.base.Preconditions;
import com.google.common.collect.BoundType;
import com.google.common.collect.Range;

import com.fasterxml.jackson.core.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import com.fasterxml.jackson.datatype.guava.deser.util.RangeFactory;
import com.fasterxml.jackson.datatype.guava.deser.util.RangeHelper;

/**
 * Jackson deserializer for a Guava {@link Range}.
 *<p>
 * TODO: I think it would make sense to reimplement this deserializer to
 * use Delegating Deserializer, using a POJO as an intermediate form (properties
 * could be of type {@link java.lang.Object})
 * This would also also simplify the implementation a bit.
 */
public class RangeDeserializer
    extends StdDeserializer<Range<?>>
    implements ContextualDeserializer
{
    private static final long serialVersionUID = 1L;

    protected final JavaType _rangeType;

    protected final JsonDeserializer<Object> _endpointDeserializer;

    protected final BoundType _defaultBoundType;

    /**
     * @since 2.10
     */
    protected final RangeHelper.RangeProperties _fieldNames;

    /*
    /**********************************************************
    /* Life-cycle
    /**********************************************************
     */

    public RangeDeserializer(BoundType defaultBoundType, JavaType rangeType) {
        this(rangeType, null, defaultBoundType);
    }

    @Deprecated // since 2.12
    public RangeDeserializer(JavaType rangeType, JsonDeserializer<?> endpointDeser) {
        this(rangeType, endpointDeser, null);
    }

    @Deprecated // since 2.10
    public RangeDeserializer(JavaType rangeType, JsonDeserializer<?> endpointDeser,
            BoundType defaultBoundType)
    {
        this(rangeType, endpointDeser, defaultBoundType, RangeHelper.standardNames());
    }

    /**
     * @since 2.10
     */
    @SuppressWarnings("unchecked")
    protected RangeDeserializer(JavaType rangeType, JsonDeserializer<?> endpointDeser,
            BoundType defaultBoundType, RangeHelper.RangeProperties fieldNames)
    {
        super(rangeType);
        _rangeType = rangeType;
        _endpointDeserializer = (JsonDeserializer<Object>) endpointDeser;
        _defaultBoundType = defaultBoundType;
        _fieldNames = fieldNames;
    }

    @Override
    public JavaType getValueType() { return _rangeType; }

    @Override // since 2.12
    public LogicalType logicalType() {
        // 30-May-2020, tatu: Not 100% sure, but looks a bit like POJO so...
        return LogicalType.POJO;
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt,
            BeanProperty property) throws JsonMappingException
    {
        final RangeHelper.RangeProperties fieldNames = RangeHelper.getPropertyNames(ctxt.getConfig(),
                ctxt.getConfig().getPropertyNamingStrategy());
        JsonDeserializer<?> deser = _endpointDeserializer;
        if (deser == null) {
            JavaType endpointType = _rangeType.containedType(0);
            if (endpointType == null) { // should this ever occur?
                endpointType = TypeFactory.unknownType();
            }
            deser = ctxt.findContextualValueDeserializer(endpointType, property);
        } else if (deser instanceof ContextualDeserializer) {
            // 04-Sep-2019, tatu: If we already have a deserialize, should contextualize, right?
            deser = ((ContextualDeserializer) deser).createContextual(ctxt, property);
        }
        if ((deser != _endpointDeserializer) || (fieldNames != _fieldNames)) {
            return new RangeDeserializer(_rangeType, deser, _defaultBoundType, fieldNames);
        }
        return this;
    }

    /*
    /**********************************************************
    /* Actual deserialization
    /**********************************************************
     */

    @Override
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt,
            TypeDeserializer typeDeserializer)
        throws IOException
    {
        return typeDeserializer.deserializeTypedFromObject(p, ctxt);
    }

    @Override
    public Range<?> deserialize(JsonParser p, DeserializationContext context)
        throws IOException
    {
        // NOTE: either START_OBJECT _or_ FIELD_NAME fine; latter for polymorphic cases
        JsonToken t = p.getCurrentToken();
        if (t == JsonToken.START_OBJECT) {
            t = p.nextToken();
        }

        Comparable<?> lowerEndpoint = null;
        Comparable<?> upperEndpoint = null;
        BoundType lowerBoundType = _defaultBoundType;
        BoundType upperBoundType = _defaultBoundType;

        for (; t != JsonToken.END_OBJECT; t = p.nextToken()) {
            expect(context, JsonToken.FIELD_NAME, t);
            String fieldName = p.getCurrentName();
            try {
                if (fieldName.equals(_fieldNames.lowerEndpoint)) {
                    p.nextToken();
                    lowerEndpoint = deserializeEndpoint(context, p);
                } else if (fieldName.equals(_fieldNames.upperEndpoint)) {
                    p.nextToken();
                    upperEndpoint = deserializeEndpoint(context, p);
                } else if (fieldName.equals(_fieldNames.lowerBoundType)) {
                    p.nextToken();
                    lowerBoundType = deserializeBoundType(context, p);
                } else if (fieldName.equals(_fieldNames.upperBoundType)) {
                    p.nextToken();
                    upperBoundType = deserializeBoundType(context, p);
                } else {
                    // Note: should either return `true`, iff problem is handled (and
                    // content processed or skipped) or throw exception. So if we
                    // get back, we ought to be fine...
                    context.handleUnknownProperty(p, this, Range.class, fieldName);
                }
            } catch (IllegalStateException e) {
                context.reportBadDefinition(handledType(), e.getMessage());
                return null;
            }
        }
        try {
            if ((lowerEndpoint != null) && (upperEndpoint != null)) {
                Preconditions.checkState(lowerEndpoint.getClass() == upperEndpoint.getClass(),
                        "Endpoint types are not the same - 'lowerEndpoint' deserialized to [%s], and 'upperEndpoint' deserialized to [%s].",
                        lowerEndpoint.getClass().getName(),
                        upperEndpoint.getClass().getName());
                Preconditions.checkState(lowerBoundType != null, "'lowerEndpoint' field found, but not 'lowerBoundType'");
                Preconditions.checkState(upperBoundType != null, "'upperEndpoint' field found, but not 'upperBoundType'");
                return RangeFactory.range(lowerEndpoint, lowerBoundType, upperEndpoint, upperBoundType);
            }
            if (lowerEndpoint != null) {
                Preconditions.checkState(lowerBoundType != null, "'lowerEndpoint' field found, but not 'lowerBoundType'");
                return RangeFactory.downTo(lowerEndpoint, lowerBoundType);
            }
            if (upperEndpoint != null) {
                Preconditions.checkState(upperBoundType != null, "'upperEndpoint' field found, but not 'upperBoundType'");
                return RangeFactory.upTo(upperEndpoint, upperBoundType);
            }
            return RangeFactory.all();
        } catch (IllegalStateException e) {
            context.reportBadDefinition(handledType(), e.getMessage());
            return null;
        }
    }

    private BoundType deserializeBoundType(DeserializationContext context, JsonParser p) throws IOException
    {
        expect(context, JsonToken.VALUE_STRING, p.getCurrentToken());
        String name = p.getText();
        try {
            if (context.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS) && name != null) {
                return BoundType.valueOf(name.toUpperCase());
            }
            return BoundType.valueOf(name);
        } catch (IllegalArgumentException e) {
            return (BoundType) context.handleWeirdStringValue(BoundType.class, name,
                    "not a valid BoundType name (should be one oF: %s)",
                    Arrays.asList(BoundType.values()));
        }
    }

    private Comparable<?> deserializeEndpoint(DeserializationContext context, JsonParser p) throws IOException
    {
        Object obj = _endpointDeserializer.deserialize(p, context);
        if (!(obj instanceof Comparable)) {
            // 01-Oct-2015, tatu: Not sure if it's data or definition problem... for now,
            //    assume definition, but may need to reconsider
            context.reportBadDefinition(_rangeType,
                    String.format(
                            "Field [%s] deserialized to [%s], which does not implement Comparable.",
                            p.getCurrentName(), obj.getClass().getName()));
        }
        return (Comparable<?>) obj;
    }

    private void expect(DeserializationContext context, JsonToken expected, JsonToken actual) throws JsonMappingException
    {
        if (actual != expected) {
            context.reportInputMismatch(this, String.format("Problem deserializing %s: expecting %s, found %s",
                    handledType().getName(), expected, actual));
        }
    }
}
