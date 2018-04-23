package com.fasterxml.jackson.datatype.eclipsecollections.deser.set;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.datatype.eclipsecollections.deser.BaseCollectionDeserializer;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.primitive.BooleanSet;
import org.eclipse.collections.api.set.primitive.ByteSet;
import org.eclipse.collections.api.set.primitive.CharSet;
import org.eclipse.collections.api.set.primitive.DoubleSet;
import org.eclipse.collections.api.set.primitive.FloatSet;
import org.eclipse.collections.api.set.primitive.IntSet;
import org.eclipse.collections.api.set.primitive.LongSet;
import org.eclipse.collections.api.set.primitive.MutableBooleanSet;
import org.eclipse.collections.api.set.primitive.MutableByteSet;
import org.eclipse.collections.api.set.primitive.MutableCharSet;
import org.eclipse.collections.api.set.primitive.MutableDoubleSet;
import org.eclipse.collections.api.set.primitive.MutableFloatSet;
import org.eclipse.collections.api.set.primitive.MutableIntSet;
import org.eclipse.collections.api.set.primitive.MutableLongSet;
import org.eclipse.collections.api.set.primitive.MutableShortSet;
import org.eclipse.collections.api.set.primitive.ShortSet;
import org.eclipse.collections.impl.factory.Sets;
import org.eclipse.collections.impl.factory.primitive.BooleanSets;
import org.eclipse.collections.impl.factory.primitive.ByteSets;
import org.eclipse.collections.impl.factory.primitive.CharSets;
import org.eclipse.collections.impl.factory.primitive.DoubleSets;
import org.eclipse.collections.impl.factory.primitive.FloatSets;
import org.eclipse.collections.impl.factory.primitive.IntSets;
import org.eclipse.collections.impl.factory.primitive.LongSets;
import org.eclipse.collections.impl.factory.primitive.ShortSets;

public final class MutableSetDeserializer {
    private MutableSetDeserializer() {
    }

    public static final class Ref extends
            BaseCollectionDeserializer.Ref<MutableSet<?>, MutableSet<Object>> {
        public Ref(JavaType elementType, TypeDeserializer typeDeserializer, JsonDeserializer<?> deserializer) {
            super(MutableSet.class, elementType, typeDeserializer, deserializer);
        }

        @Override
        protected MutableSet<Object> createIntermediate() {
            return Sets.mutable.empty();
        }

        @Override
        protected MutableSet<?> finish(MutableSet<Object> objects) {
            return objects;
        }

        @Override
        protected Ref<?, ?> withResolved(
                TypeDeserializer typeDeserializerForValue,
                JsonDeserializer<?> valueDeserializer
        ) {
            return new MutableSetDeserializer.Ref(_elementType, typeDeserializerForValue, valueDeserializer);
        }
    }

    public static final class Boolean extends
            BaseCollectionDeserializer.Boolean<MutableBooleanSet, MutableBooleanSet> {
        public static final MutableSetDeserializer.Boolean INSTANCE = new MutableSetDeserializer.Boolean();

        public Boolean() {
            super(BooleanSet.class);
        }

        @Override
        protected MutableBooleanSet createIntermediate() {
            return BooleanSets.mutable.empty();
        }

        @Override
        protected MutableBooleanSet finish(MutableBooleanSet objects) {
            return objects;
        }
    }

    public static final class Byte extends
            BaseCollectionDeserializer.Byte<MutableByteSet, MutableByteSet> {
        public static final MutableSetDeserializer.Byte INSTANCE = new MutableSetDeserializer.Byte();

        public Byte() {
            super(ByteSet.class);
        }

        @Override
        protected MutableByteSet createIntermediate() {
            return ByteSets.mutable.empty();
        }

        @Override
        protected MutableByteSet finish(MutableByteSet objects) {
            return objects;
        }
    }

    public static final class Short extends
            BaseCollectionDeserializer.Short<MutableShortSet, MutableShortSet> {
        public static final MutableSetDeserializer.Short INSTANCE = new MutableSetDeserializer.Short();

        public Short() {
            super(ShortSet.class);
        }

        @Override
        protected MutableShortSet createIntermediate() {
            return ShortSets.mutable.empty();
        }

        @Override
        protected MutableShortSet finish(MutableShortSet objects) {
            return objects;
        }
    }

    public static final class Char extends
            BaseCollectionDeserializer.Char<MutableCharSet, MutableCharSet> {
        public static final MutableSetDeserializer.Char INSTANCE = new MutableSetDeserializer.Char();

        public Char() {
            super(CharSet.class);
        }

        @Override
        protected MutableCharSet createIntermediate() {
            return CharSets.mutable.empty();
        }

        @Override
        protected MutableCharSet finish(MutableCharSet objects) {
            return objects;
        }
    }

    public static final class Int extends
            BaseCollectionDeserializer.Int<MutableIntSet, MutableIntSet> {
        public static final MutableSetDeserializer.Int INSTANCE = new MutableSetDeserializer.Int();

        public Int() {
            super(IntSet.class);
        }

        @Override
        protected MutableIntSet createIntermediate() {
            return IntSets.mutable.empty();
        }

        @Override
        protected MutableIntSet finish(MutableIntSet objects) {
            return objects;
        }
    }

    public static final class Float extends
            BaseCollectionDeserializer.Float<MutableFloatSet, MutableFloatSet> {
        public static final MutableSetDeserializer.Float INSTANCE = new MutableSetDeserializer.Float();

        public Float() {
            super(FloatSet.class);
        }

        @Override
        protected MutableFloatSet createIntermediate() {
            return FloatSets.mutable.empty();
        }

        @Override
        protected MutableFloatSet finish(MutableFloatSet objects) {
            return objects;
        }
    }

    public static final class Long extends
            BaseCollectionDeserializer.Long<MutableLongSet, MutableLongSet> {
        public static final MutableSetDeserializer.Long INSTANCE = new MutableSetDeserializer.Long();

        public Long() {
            super(LongSet.class);
        }

        @Override
        protected MutableLongSet createIntermediate() {
            return LongSets.mutable.empty();
        }

        @Override
        protected MutableLongSet finish(MutableLongSet objects) {
            return objects;
        }
    }

    public static final class Double extends
            BaseCollectionDeserializer.Double<MutableDoubleSet, MutableDoubleSet> {
        public static final MutableSetDeserializer.Double INSTANCE = new MutableSetDeserializer.Double();

        public Double() {
            super(DoubleSet.class);
        }

        @Override
        protected MutableDoubleSet createIntermediate() {
            return DoubleSets.mutable.empty();
        }

        @Override
        protected MutableDoubleSet finish(MutableDoubleSet objects) {
            return objects;
        }
    }
}
