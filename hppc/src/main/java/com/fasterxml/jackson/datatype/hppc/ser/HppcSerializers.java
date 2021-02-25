package com.fasterxml.jackson.datatype.hppc.ser;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ValueSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.ser.jdk.ObjectArraySerializer;
import com.fasterxml.jackson.databind.type.CollectionLikeType;

import com.carrotsearch.hppc.ObjectContainer;

public class HppcSerializers extends Serializers.Base
{
    public HppcSerializers() { }
    
    @Override
    public ValueSerializer<?> findCollectionLikeSerializer(SerializationConfig config,
            CollectionLikeType containerType, BeanDescription beanDesc, JsonFormat.Value formatOverrides,
            TypeSerializer elementTypeSerializer, ValueSerializer<Object> elementValueSerializer)
    {
        if (ObjectContainer.class.isAssignableFrom(containerType.getRawClass())) {
            // hmmh. not sure if we can find 'forceStaticTyping' anywhere...
            boolean staticTyping = config.isEnabled(MapperFeature.USE_STATIC_TYPING);
            ObjectArraySerializer ser = new ObjectArraySerializer(containerType.getContentType(),
                    staticTyping, elementTypeSerializer, elementValueSerializer);
            return new ObjectContainerSerializer(containerType, ser);
        }
        return null;
    }

    /*
    @Override
    public JsonSerializer<?> findMapLikeSerializer(SerializationConfig config,
            MapLikeType arg1, BeanDescription arg2, JsonFormat.Value formatOverrides,
            JsonSerializer<Object> arg4, TypeSerializer arg5,
            JsonSerializer<Object> arg6) {
        // TODO: handle XxxMap with Object keys and/or values
        return null;
    }
    */

    /**
     * Anything that we don't explicitly mark as Map- or Collection-like
     * will end up here...
     */
    @Override
    public ValueSerializer<?> findSerializer(SerializationConfig config,
            JavaType type, BeanDescription beanDesc, JsonFormat.Value formatOverrides)
    {
        return HppcContainerSerializers.getMatchingSerializer(config, type);
    }
}
