package com.fasterxml.jackson.datatype.guava.deser;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

import com.google.common.collect.LinkedHashMultiset;

public class LinkedHashMultisetDeserializer
    extends GuavaMultisetDeserializer<LinkedHashMultiset<Object>>
{
    private static final long serialVersionUID = 1L;

    public LinkedHashMultisetDeserializer(JavaType selfType,
            JsonDeserializer<?> deser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle) {
        super(selfType, deser, typeDeser, nuller, unwrapSingle);
    }

    @Override
    protected LinkedHashMultiset<Object> createMultiset() {
        return LinkedHashMultiset.create();
    }

    @Override
    public GuavaCollectionDeserializer<LinkedHashMultiset<Object>> withResolved(JsonDeserializer<?> valueDeser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle) {
        return new LinkedHashMultisetDeserializer(_containerType,
                valueDeser, typeDeser, nuller, unwrapSingle);
    }
}
