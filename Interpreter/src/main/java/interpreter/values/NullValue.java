package interpreter.values;

public record NullValue() implements RuntimeValue {
    @Override
    public ValueType type() {
        return ValueType.nullValue;
    }
}