package com.fasterxml.jackson.datatype.guava;

import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.ForwardingCache;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.*;
import com.google.common.hash.HashCode;
import com.google.common.net.HostAndPort;
import com.google.common.net.InternetDomainName;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.datatype.guava.deser.*;
import com.fasterxml.jackson.datatype.guava.deser.multimap.list.ArrayListMultimapDeserializer;
import com.fasterxml.jackson.datatype.guava.deser.multimap.list.LinkedListMultimapDeserializer;
import com.fasterxml.jackson.datatype.guava.deser.multimap.set.HashMultimapDeserializer;
import com.fasterxml.jackson.datatype.guava.deser.multimap.set.LinkedHashMultimapDeserializer;

import java.io.Serializable;

/**
 * Custom deserializers module offers.
 */
public class GuavaDeserializers
    extends Deserializers.Base
    implements Serializable
{
    static final long serialVersionUID = 1L;
    protected BoundType _defaultBoundType;

    public GuavaDeserializers() {
        this(null);
    }

    public GuavaDeserializers(BoundType defaultBoundType) {
        _defaultBoundType = defaultBoundType;
    }

    /**
     * We have plenty of collection types to support...
     */
    @Override
    public JsonDeserializer<?> findCollectionDeserializer(CollectionType type,
            DeserializationConfig config, BeanDescription beanDesc,
            TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer)
        throws JsonMappingException
    {
        Class<?> raw = type.getRawClass();

        // ImmutableXxx types?
        if (ImmutableCollection.class.isAssignableFrom(raw)) {
            if (ImmutableList.class.isAssignableFrom(raw)) {
                return new ImmutableListDeserializer(type,
                        elementDeserializer, elementTypeDeserializer,
                        null, null);
            }
            if (ImmutableMultiset.class.isAssignableFrom(raw)) {
                // sorted one?
                if (ImmutableSortedMultiset.class.isAssignableFrom(raw)) {
                    /* See considerations for ImmutableSortedSet below. */
                    requireCollectionOfComparableElements(type, "ImmutableSortedMultiset");
                    return new ImmutableSortedMultisetDeserializer(type,
                            elementDeserializer, elementTypeDeserializer,
                            null, null);
                }
                // nah, just regular one
                return new ImmutableMultisetDeserializer(type,
                        elementDeserializer, elementTypeDeserializer,
                        null, null);
            }
            if (ImmutableSet.class.isAssignableFrom(raw)) {
                // sorted one?
                if (ImmutableSortedSet.class.isAssignableFrom(raw)) {
                    /* 28-Nov-2010, tatu: With some more work would be able to use other things
                     *   than natural ordering; but that'll have to do for now...
                     */
                    requireCollectionOfComparableElements(type, "ImmutableSortedSet");
                    return new ImmutableSortedSetDeserializer(type,
                            elementDeserializer, elementTypeDeserializer,
                            null, null);
                }
                // nah, just regular one
                return new ImmutableSetDeserializer(type,
                        elementDeserializer, elementTypeDeserializer,
                        null, null);
            }
            // TODO: make configurable (for now just default blindly to a list)
            return new ImmutableListDeserializer(type,
                    elementDeserializer, elementTypeDeserializer,
                    null, null);
        }

        // Multi-xxx collections?
        if (Multiset.class.isAssignableFrom(raw)) {
            if (SortedMultiset.class.isAssignableFrom(raw)) {
                if (TreeMultiset.class.isAssignableFrom(raw)) {
                    return new TreeMultisetDeserializer(type,
                            elementDeserializer, elementTypeDeserializer,
                            null, null);
                }

                // TODO: make configurable (for now just default blindly)
                return new TreeMultisetDeserializer(type,
                        elementDeserializer, elementTypeDeserializer,
                        null, null);
            }

            // Quite a few variations...
            if (LinkedHashMultiset.class.isAssignableFrom(raw)) {
                return new LinkedHashMultisetDeserializer(type,
                        elementDeserializer, elementTypeDeserializer,
                        null, null);
           }
            if (HashMultiset.class.isAssignableFrom(raw)) {
                return new HashMultisetDeserializer(type,
                        elementDeserializer, elementTypeDeserializer,
                        null, null);
            }
            if (EnumMultiset.class.isAssignableFrom(raw)) {
                // !!! TODO
            }

            // TODO: make configurable (for now just default blindly)
            return new HashMultisetDeserializer(type,
                    elementDeserializer, elementTypeDeserializer,
                    null, null);
        }

        return null;
    }

    private void requireCollectionOfComparableElements(CollectionType actualType, String targetType) {
        Class<?> elemType = actualType.getContentType().getRawClass();
        if (!Comparable.class.isAssignableFrom(elemType)) {
            throw new IllegalArgumentException("Can not handle " + targetType
                    + " with elements that are not Comparable<?> (" + elemType.getName() + ")");
        }
    }

    /**
     * A few Map types to support.
     */
    @Override
    public JsonDeserializer<?> findMapDeserializer(MapType type,
            DeserializationConfig config, BeanDescription beanDesc,
            KeyDeserializer keyDeserializer,
            TypeDeserializer valueTypeDeserializer, JsonDeserializer<?> valueDeserializer)
        throws JsonMappingException
    {
        Class<?> raw = type.getRawClass();

        // ImmutableXxxMap types?
        if (ImmutableMap.class.isAssignableFrom(raw)) {
            if (ImmutableSortedMap.class.isAssignableFrom(raw)) {
                return new ImmutableSortedMapDeserializer(type, keyDeserializer,
                        valueDeserializer, valueTypeDeserializer, null);
            }
            if (ImmutableBiMap.class.isAssignableFrom(raw)) {
                return new ImmutableBiMapDeserializer(type, keyDeserializer,
                        valueDeserializer, valueTypeDeserializer, null);
            }
            // Otherwise, plain old ImmutableMap...
            return new ImmutableMapDeserializer(type, keyDeserializer,
                    valueDeserializer, valueTypeDeserializer, null);
        }

        // XxxBiMap types?
        if (BiMap.class.isAssignableFrom(raw)) {
            if (EnumBiMap.class.isAssignableFrom(raw)) {
                // !!! TODO
            }
            if (EnumHashBiMap.class.isAssignableFrom(raw)) {
                // !!! TODO
            }
            if (HashBiMap.class.isAssignableFrom(raw)) {
                // !!! TODO
            }
            // !!! TODO default
        }


        return null;
    }

    @Override
    public JsonDeserializer<?> findMapLikeDeserializer(MapLikeType type,
            DeserializationConfig config, BeanDescription beanDesc,
            KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer,
            JsonDeserializer<?> elementDeserializer)
        throws JsonMappingException
    {
        Class<?> raw = type.getRawClass();

        // ListMultimaps
        if (ListMultimap.class.isAssignableFrom(raw)) {
            if (ImmutableListMultimap.class.isAssignableFrom(raw)) {
                // TODO
            }
            if (ArrayListMultimap.class.isAssignableFrom(raw)) {
                return new ArrayListMultimapDeserializer(type, keyDeserializer,
                        elementTypeDeserializer, elementDeserializer);
            }
            if (LinkedListMultimap.class.isAssignableFrom(raw)) {
                return new LinkedListMultimapDeserializer(type, keyDeserializer,
                        elementTypeDeserializer, elementDeserializer);
            }
            if (ForwardingListMultimap.class.isAssignableFrom(raw)) {
                // TODO
            }

            // TODO: Remove the default fall-through once all implementations are in place.
            return new ArrayListMultimapDeserializer(type, keyDeserializer,
                    elementTypeDeserializer, elementDeserializer);
        }

        // SetMultimaps
        if (SetMultimap.class.isAssignableFrom(raw)) {

            // SortedSetMultimap
            if (SortedSetMultimap.class.isAssignableFrom(raw)) {
                if (TreeMultimap.class.isAssignableFrom(raw)) {
                    // TODO
                }
                if (ForwardingSortedSetMultimap.class.isAssignableFrom(raw)) {
                    // TODO
                }
            }

            if (ImmutableSetMultimap.class.isAssignableFrom(raw)) {
                // [#67]: Preserve order of entries
                return new LinkedHashMultimapDeserializer(type, keyDeserializer,
                        elementTypeDeserializer, elementDeserializer);
            }
            if (HashMultimap.class.isAssignableFrom(raw)) {
                return new HashMultimapDeserializer(type, keyDeserializer, elementTypeDeserializer,
                        elementDeserializer);
            }
            if (LinkedHashMultimap.class.isAssignableFrom(raw)) {
                return new LinkedHashMultimapDeserializer(type, keyDeserializer,
                        elementTypeDeserializer, elementDeserializer);
            }
            if (ForwardingSetMultimap.class.isAssignableFrom(raw)) {
                // TODO
            }

            // TODO: Remove the default fall-through once all implementations are covered.
            return new LinkedHashMultimapDeserializer(type, keyDeserializer, elementTypeDeserializer,
                    elementDeserializer);
        }

        // Handle the case where nothing more specific was provided.
        if (Multimap.class.isAssignableFrom(raw)) {
            return new LinkedListMultimapDeserializer(type, keyDeserializer,
                    elementTypeDeserializer, elementDeserializer);
        }

        if (Table.class.isAssignableFrom(raw)) {
            // !!! TODO
        }
        // @since 2.16 : support Cache deserialization
        java.util.Optional<JsonDeserializer<?>> cacheDeserializer = findCacheDeserializer(raw, type, config, 
                                        beanDesc, keyDeserializer, elementTypeDeserializer, elementDeserializer);
        if (cacheDeserializer.isPresent()) {
            return cacheDeserializer.get();
        }

        return null;
    }

    /**
     * Find matching implementation of {@link Cache} deserializers by checking
     * if the parameter {@code raw} type is assignable.
     *
     * NOTE: Make sure the cache implementations are checked in such a way that more concrete classes are
     * compared first before more abstract ones.
     *
     * @return An optional {@link JsonDeserializer} for the cache type, if found.
     * @since 2.16
     */
    private java.util.Optional<JsonDeserializer<?>> findCacheDeserializer(Class<?> raw, MapLikeType type, 
        DeserializationConfig config, BeanDescription beanDesc, KeyDeserializer keyDeserializer, 
        TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) 
    {
        /* // Example implementations
        if (LoadingCache.class.isAssignableFrom(raw)) {
            return ....your implementation....;
        }
        */
        if (Cache.class.isAssignableFrom(raw)) {
            return java.util.Optional.of(
                new SimpleCacheDeserializer(type, keyDeserializer, elementTypeDeserializer, elementDeserializer));
        }
        return java.util.Optional.empty();
    }

    @Override // since 2.7
    public JsonDeserializer<?> findReferenceDeserializer(ReferenceType refType,
            DeserializationConfig config, BeanDescription beanDesc,
            TypeDeserializer contentTypeDeserializer, JsonDeserializer<?> contentDeserializer)
    {
        // 28-Oct-2016, tatu: Should try to support subtypes too, with ValueInstantiators, but
        //   not 100% clear how this could work at this point
//        if (refType.isTypeOrSubTypeOf(Optional.class)) {
        if (refType.hasRawClass(Optional.class)) {
            return new GuavaOptionalDeserializer(refType, null, contentTypeDeserializer, contentDeserializer);
        }
        return null;
    }

    @Override
    public JsonDeserializer<?> findBeanDeserializer(final JavaType type, DeserializationConfig config,
            BeanDescription beanDesc)
    {
        if (type.hasRawClass(RangeSet.class)) {
            return new RangeSetDeserializer();
        }
        if (type.hasRawClass(Range.class)) {
            return new RangeDeserializer(_defaultBoundType, type);
        }
        if (type.hasRawClass(HostAndPort.class)) {
            return HostAndPortDeserializer.std;
        }
        if (type.hasRawClass(InternetDomainName.class)) {
            return InternetDomainNameDeserializer.std;
        }
        if (type.hasRawClass(HashCode.class)) {
            return HashCodeDeserializer.std;
        }
        return null;
    }

    @Override
    public boolean hasDeserializerFor(DeserializationConfig config, Class<?> valueType) {
        if (valueType.getName().startsWith("com.google.")) {
            return (valueType == Optional.class)
                    || (valueType == RangeSet.class)
                    || (valueType == HostAndPort.class)
                    || (valueType == InternetDomainName.class)
                    || (valueType == HashCode.class)
                    // Ok to claim we might support; not a guarantee
                    || Multiset.class.isAssignableFrom(valueType)
                    || Multimap.class.isAssignableFrom(valueType)
                    || ImmutableCollection.class.isAssignableFrom(valueType)
                    || ImmutableMap.class.isAssignableFrom(valueType)
                    || BiMap.class.isAssignableFrom(valueType)
                    ;
        }
        return false;
    }
}
