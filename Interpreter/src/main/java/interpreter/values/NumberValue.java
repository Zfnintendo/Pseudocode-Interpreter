package interpreter.values;

public record NumberValue(double value) implements RuntimeValue {
    @Override
    public ValueType type() {
        return ValueType.number;
    }
}