package com.fasterxml.jackson.datatype.pcollections.deser;


import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.KeyDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.type.MapType;
import org.pcollections.HashPMap;
import org.pcollections.HashTreePMap;

public class HashTreePMapDeserializer
 extends PCollectionsMapDeserializer<HashPMap<Object, Object>>
{
    public HashTreePMapDeserializer(MapType type, KeyDeserializer keyDeser,
            TypeDeserializer typeDeser, ValueDeserializer<?> deser)
    {
        super(type, keyDeser, typeDeser, deser);
    }

    @Override
    public HashTreePMapDeserializer withResolved(KeyDeserializer keyDeser,
            TypeDeserializer typeDeser, ValueDeserializer<?> valueDeser) {
        return new HashTreePMapDeserializer(_mapType, keyDeser,
                typeDeser, valueDeser);
    }

    @Override
    protected HashPMap<Object, Object> createEmptyMap() {
        return HashTreePMap.empty();
    }
}
