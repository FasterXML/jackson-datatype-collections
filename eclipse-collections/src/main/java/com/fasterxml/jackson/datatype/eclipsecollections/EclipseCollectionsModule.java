package com.fasterxml.jackson.datatype.eclipsecollections;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.deser.ValueInstantiators;
import com.fasterxml.jackson.datatype.eclipsecollections.deser.pair.PairInstantiators;

/**
 * Basic Jackson {@link Module} that adds support for eclipse-collections types.
 */
public class EclipseCollectionsModule extends Module {
    private final String NAME = "EclipseCollectionsModule";

    public EclipseCollectionsModule() {
        super();
    }

    @Override
    public String getModuleName() {
        return NAME;
    }

    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }

    @Override
    public void setupModule(SetupContext context) {
        context.addDeserializers(new EclipseCollectionsDeserializers());
        context.addSerializers(new EclipseCollectionsSerializers());

        context.addValueInstantiators(new PairInstantiators());
    }

    @Override
    public int hashCode() {
        return NAME.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }
}
