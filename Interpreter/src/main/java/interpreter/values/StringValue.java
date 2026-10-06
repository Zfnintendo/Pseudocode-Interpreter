package interpreter.values;

import java.util.Objects;

public record StringValue(String value) implements RuntimeValue {
    public StringValue {
        Objects.requireNonNull(value);
    }

    @Override
    public ValueType type() {
        return ValueType.string;
    }
}
