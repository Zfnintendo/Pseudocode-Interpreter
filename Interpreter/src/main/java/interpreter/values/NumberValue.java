package interpreter.values;

public class NumberValue implements RuntimeValue {

    private final double value;

    public NumberValue(double value) {
        this.value = value;
    }

    public ValueType type() {
        return ValueType.number;
    }

    public double value() {
        return this.value;
    }

}
