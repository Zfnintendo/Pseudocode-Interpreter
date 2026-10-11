package interpreter.values;

import ast.ScalarType;
import interpreter.runtime.VariableEntry;
import java.util.Objects;

public class ArrayValue implements RuntimeValue {

    private final ScalarType elementType;
    private final int[] lowerBounds;
    private final int[] upperBounds;
    private final int[] sizes;
    private final VariableEntry[] elements;

    public ArrayValue(ScalarType elementType, int[] lowerBounds, int[] upperBounds) {
        this.elementType = Objects.requireNonNull(elementType);

        int dimensions = lowerBounds.length;

        if (dimensions < 1 || dimensions > 2 || dimensions != upperBounds.length) {
            throw new IllegalArgumentException("Arrays require one or two pairs of bounds.");
        }

        this.lowerBounds = lowerBounds.clone();
        this.upperBounds = upperBounds.clone();
        this.sizes = new int[dimensions];

        long totalSize = 1;

        for (int i = 0; i < dimensions; i++) {
            long size = (long) this.upperBounds[i] - this.lowerBounds[i] + 1;

            if (size <= 0) {
                throw new IllegalArgumentException("Lower bound must not exceed upper bound.");
            }

            if (size > Integer.MAX_VALUE / totalSize) {
                throw new IllegalArgumentException("Array is too large.");
            }

            sizes[i] = (int) size;
            totalSize *= size;
        }

        this.elements = new VariableEntry[(int) totalSize];

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

    public int dimensions() {
        return lowerBounds.length;
    }

    private int offset(int... indices) {
        if (indices.length != this.dimensions()) {
            throw new IllegalArgumentException("Expected " + dimensions() + " indices, got " + indices.length);
        }

        int offset = 0;

        for (int i = 0; i < indices.length; i++) {
            if (indices[i] < lowerBounds[i] || indices[i] > upperBounds[i]) {
                throw new IllegalArgumentException("Index " + indices[i] + " is outside " + lowerBounds[i] + ":" + upperBounds[i] + " in dimension " + (i+1));
            }

            offset = offset * sizes[i] + (indices[i] - lowerBounds[i]);
        }

        return offset;
    }

    public RuntimeValue read(int... indices) {
        return this.elements[this.offset(indices)].value();
    }

    public void assign(RuntimeValue value, int... indices) {
        this.elements[this.offset(indices)].assign(value);
    }

    public void validateIndices(int... indices) {
        offset(indices);
    }

}
