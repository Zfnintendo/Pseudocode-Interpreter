package interpreter.values;

public record BooleanValue(boolean value) implements RuntimeValue {
    @Override
    public ValueType type() {
        return ValueType.booleanValue;
    }
}
