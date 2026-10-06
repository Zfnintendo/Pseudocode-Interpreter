package interpreter.values;

public record CharValue(char value) implements RuntimeValue {
    @Override
    public ValueType type() {
        return ValueType.character;
    }
}
