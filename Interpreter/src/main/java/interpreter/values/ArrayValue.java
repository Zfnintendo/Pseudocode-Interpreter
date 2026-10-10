package interpreter.values;

import ast.ScalarType;
import interpreter.runtime.VariableEntry;

public class ArrayValue implements RuntimeValue {

    private final ScalarType elementType;
    private final int lowerBound;
    private final int upperBound;
    private final VariableEntry[] elements;

    public ArrayValue(ScalarType elementType, int lowerBound, int upperBound) {
        long size = (long) upperBound - lowerBound + 1;

        if (size <= 0 || size > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Invalid array bounds.");
        }

        this.elementType = elementType;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.elements = new VariableEntry[(int) size];

        for (int i = 0; i < this.elements.length; i++) {
            elements[i] = new VariableEntry(elementType);
        }
    }

    @Override
    public ValueType type() {
        return ValueType.array;
    }

    public ScalarType elementType() {
        return this.elementType;
    }

    private int offset(int index) {
        if (index < lowerBound || index > upperBound) {
            throw new IllegalArgumentException("Array index " + index + " is outside " + this.lowerBound + ":" + upperBound);
        }

        return index - this.lowerBound;
    }

    public RuntimeValue read(int index) {
        return this.elements[this.offset(index)].value();
    }

    public void assign(int index, RuntimeValue value) {
        this.elements[this.offset(index)].assign(value);
    }

}
