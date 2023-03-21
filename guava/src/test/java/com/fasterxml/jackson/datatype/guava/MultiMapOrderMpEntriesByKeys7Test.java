package com.fasterxml.jackson.datatype.guava;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotEquals;

// [jackson-datatype-collections#7]: [Guava] Add support for WRITE_SORTED_MAP_ENTRIES
public class MultiMapOrderMpEntriesByKeys7Test extends ModuleTestBase {
/*
    /**********************************************************
    /* Set up
    /**********************************************************
     */

    static class UncomparableBean {
        public String value;

        public UncomparableBean(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    static class PolymorphicWrapperBean {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
        public Object aProperty;

        @Override
        public String toString() {
            return aProperty.toString();
        }
    }

    /*
    /**********************************************************
    /* Tests
    /**********************************************************
     */

    private final ObjectMapper MAPPER = mapperWithModule();

    public void testMultimapSerializeOrderedByKey() throws Exception {
        final Multimap<Object, Object> multimap = HashMultimap.create();
        multimap.put("c_key", 1);
        multimap.put("d_key", 1);
        multimap.put("a_key", 1);
        multimap.put("e_key", 1);
        multimap.put("b_key", 1);

        String jsonStr = MAPPER.writer()
            .with(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
            .writeValueAsString(multimap);

        assertEquals(a2q("{'a_key':[1],'b_key':[1],'c_key':[1],'d_key':[1],'e_key':[1]}"), jsonStr);
    }

    public void testMultimapSerializeUnorderedByKey() throws Exception {
        final Multimap<Object, Object> multimap = HashMultimap.create();
        multimap.put("c_key", 1);
        multimap.put("d_key", 1);
        multimap.put("a_key", 1);
        multimap.put("e_key", 1);
        multimap.put("b_key", 1);

        String jsonStr = MAPPER.writer()
            .without(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
            .writeValueAsString(multimap);

        assertNotEquals(a2q("{'a_key':[1],'b_key':[1],'c_key':[1],'d_key':[1],'e_key':[1]}"), jsonStr);
    }


    public void testMultimapSerializeWithNullKeyFailure() throws Exception {
        final Multimap<Object, Object> multimap = HashMultimap.create();
        multimap.put("c_key", 1);
        multimap.put("d_key", 1);
        multimap.put(null, 1);
        multimap.put("e_key", 1);
        multimap.put("b_key", 1);

        try {
            MAPPER.writer()
                .with(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
                .writeValueAsString(multimap);
        } catch (JsonMappingException e) {
            verifyException(e, "[no message for java.lang.NullPointerException]");
        }
    }

    public void testMultimapSerializeUncomparablePojo() throws Exception {
        final Multimap<UncomparableBean, Integer> multimap = ArrayListMultimap.create();
        multimap.put(new UncomparableBean("c_key"), 1);
        multimap.put(new UncomparableBean("d_key"), 1);
        multimap.put(new UncomparableBean("a_key"), 1);
        multimap.put(new UncomparableBean("e_key"), 1);
        multimap.put(new UncomparableBean("b_key"), 1);

        String jsonStr = MAPPER.writer()
            .with(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
            .writeValueAsString(multimap);

        assertNotNull(a2q("{'a_key':[1],'b_key':[1],'c_key':[1],'d_key':[1],'e_key':[1]}"), jsonStr);
    }

    public void testSerializeAllTypesOfMultimapOrdered() throws Exception {
        final Multimap<String, Integer> multimap = HashMultimap.create();
        multimap.put("c_key", 1);
        multimap.put("d_key", 1);
        multimap.put("a_key", 1);
        multimap.put("e_key", 1);
        multimap.put("b_key", 1);

        List<Multimap<?, ?>> multimapImpls = new ArrayList<>();
        multimapImpls.add(TreeMultimap.create(multimap));
        multimapImpls.add(ArrayListMultimap.create(multimap));
        multimapImpls.add(HashMultimap.create(multimap));
        multimapImpls.add(LinkedHashMultimap.create(multimap));
        multimapImpls.add(LinkedListMultimap.create(multimap));
        multimapImpls.add(ImmutableListMultimap.copyOf(multimap));
        multimapImpls.add(ImmutableSetMultimap.copyOf(multimap));

        for (Multimap<?, ?> map : multimapImpls) {
            String orderedJsonStr = MAPPER.writer()
                .with(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
                .writeValueAsString(map);

            assertEquals(a2q("{'a_key':[1],'b_key':[1],'c_key':[1],'d_key':[1],'e_key':[1]}"), orderedJsonStr);
        }
    }

    public void testPolymorphicArrayMap() throws Exception {
        final Multimap<String, Integer> multimap = HashMultimap.create();
        multimap.put("c_key", 1);
        multimap.put("d_key", 1);
        multimap.put("a_key", 1);
        multimap.put("e_key", 1);
        multimap.put("b_key", 1);
        PolymorphicWrapperBean outside = new PolymorphicWrapperBean();
        outside.aProperty = multimap;

        final String jsonStr = MAPPER.writer()
            .with(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
            .writeValueAsString(outside);

        assertEquals(a2q("{'aProperty':{'type':'HashMultimap'," +
            "'a_key':[1],'b_key':[1],'c_key':[1],'d_key':[1],'e_key':[1]}}"), jsonStr);
    }
}

