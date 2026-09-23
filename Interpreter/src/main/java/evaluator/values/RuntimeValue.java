package evaluator.values;

public class RuntimeValue {

    private ValueType valueType;

    public RuntimeValue(ValueType valueType) {
        this.valueType = valueType;
    }

    public ValueType getValueType() {
        return this.valueType;
    }

}
