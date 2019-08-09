package com.fasterxml.jackson.datatype.guava.deser;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap.Builder;

public class ImmutableBiMapDeserializer
    extends GuavaImmutableMapDeserializer<ImmutableBiMap<Object, Object>>
{
    private static final long serialVersionUID = 2L;

    public ImmutableBiMapDeserializer(JavaType type, KeyDeserializer keyDeser,
            JsonDeserializer<?> deser, TypeDeserializer typeDeser,
            NullValueProvider nuller) {
        super(type, keyDeser, deser, typeDeser, nuller);
    }

    @Override
    protected Builder<Object, Object> createBuilder() {
        return ImmutableBiMap.builder();
    }

    @Override
    public GuavaMapDeserializer<ImmutableBiMap<Object, Object>> withResolved(KeyDeserializer keyDeser,
            JsonDeserializer<?> valueDeser, TypeDeserializer typeDeser,
            NullValueProvider nuller) {
        return new ImmutableBiMapDeserializer(_containerType, keyDeser, valueDeser, typeDeser, nuller);
    }
}
