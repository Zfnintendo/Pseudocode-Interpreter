package interpreter.runtime;

import interpreter.values.RuntimeValue;

public class ReturnSignal extends RuntimeException {
    private final RuntimeValue value;

    public ReturnSignal(RuntimeValue value) {
        super(null, null, false, false);
        this.value = value;
    }

    public RuntimeValue value() {
        return value;
    }
}
