package tools.jackson.datatype.eclipsecollections.ser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;

import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.type.TypeFactory;

import org.eclipse.collections.api.DoubleIterable;
import org.eclipse.collections.api.iterator.DoubleIterator;

public final class DoubleIterableSerializer extends EclipsePrimitiveIterableSerializer<DoubleIterable> {
    private static final JavaType ELEMENT_TYPE = TypeFactory.defaultInstance().constructType(double.class);

    public DoubleIterableSerializer(BeanProperty property, Boolean unwrapSingle) {
        super(DoubleIterable.class, ELEMENT_TYPE, property, unwrapSingle);
    }

    @Override
    protected DoubleIterableSerializer withResolved(BeanProperty property, Boolean unwrapSingle) {
        return new DoubleIterableSerializer(property, unwrapSingle);
    }

    @Override
    protected void serializeContents(DoubleIterable value, JsonGenerator gen)
        throws JacksonException
    {
        DoubleIterator iterator = value.doubleIterator();
        while (iterator.hasNext()) {
            gen.writeNumber(iterator.next());
        }
    }
}