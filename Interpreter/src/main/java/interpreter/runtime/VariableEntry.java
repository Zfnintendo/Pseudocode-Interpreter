package interpreter.runtime;

import ast.TypeSpecification;
import interpreter.values.RuntimeValue;
import java.util.Objects;

public class VariableEntry {

    private final TypeSpecification declaredType;
    private final boolean constant;

    private RuntimeValue value;
    private boolean initialised;

    public VariableEntry(TypeSpecification declaredType) {
        this.declaredType = Objects.requireNonNull(declaredType);
        this.constant = false;
        this.initialised = false;
    }

    public VariableEntry(TypeSpecification declaredType, RuntimeValue initialValue, boolean constant) {
        this.declaredType = Objects.requireNonNull(declaredType);
        this.value = Objects.requireNonNull(initialValue);
        this.constant = constant;
        this.initialised = true;
    }

    public TypeSpecification declaredType() {
        return this.declaredType;
    }

    public boolean isConstant() {
        return this.constant;
    }

    public boolean isInitialised() {
        return this.initialised;
    }

    public RuntimeValue value() {
        if (!this.initialised) {
            throw new IllegalStateException("Variable has not been assigned a value.");
        }

        return this.value;
    }

    public void assign(RuntimeValue value) {
        if (this.constant) {
            throw new IllegalStateException("Can not assign a new value to a constant.");
        }

        this.value = Objects.requireNonNull(value);
        this.initialised = true;
    }

}
