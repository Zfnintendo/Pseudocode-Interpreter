package interpreter.values;

public class NullValue implements RuntimeValue {

    public ValueType type() {
        return ValueType.nullValue;
    }

    public String value() {
        return null;
    }

}
