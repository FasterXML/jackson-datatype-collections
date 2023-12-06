package com.fasterxml.jackson.datatype.guava.fuzz;

import static org.junit.Assert.assertTrue;

import org.junit.Assert;

import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.datatype.guava.ModuleTestBase;

import com.google.common.collect.ImmutableSortedMultiset;

/**
 * Unit tests for verifying the fixes for OSS-Fuzz issues
 * work as expected
 * (see [datatypes-collections#124]).
 */
public class Fuzz124_64610Test extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperWithModule();

    public void testOSSFuzzIssue64610() throws Exception
    {
        final TypeReference<?> ref = new TypeReference<ImmutableSortedMultiset<String>>() {};
        MismatchedInputException e = Assert.assertThrows(
                MismatchedInputException.class,
            () ->  MAPPER.readValue("[null]", ref));
        assertTrue(e.getMessage().contains("Guava `ImmutableCollection`s cannot contain `null`s"));
    }
}
