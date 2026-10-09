package interpreter.runtime;

import ast.TypeSpecification;
import ast.ScalarType;
import interpreter.values.*;
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
        this.constant = constant;
        validateValue(initialValue);
        this.value = initialValue;
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

        validateValue(value);
        this.value = value;
        this.initialised = true;
    }

    private void validateValue(RuntimeValue value) {
        Objects.requireNonNull(value);

        if (!(this.declaredType instanceof ScalarType(lexer.TokenEnums tokenType))) {
            throw new UnsupportedOperationException("Only scalar assignments are supported yet.");
        }

        boolean valid = switch (tokenType) {
            case typeInteger -> value instanceof NumberValue(double value1) && Double.isFinite(value1) && value1 == Math.rint(value1);
            case typeReal -> value instanceof NumberValue;
            case typeString -> value instanceof StringValue;
            case typeChar -> value instanceof CharValue;
            case typeBoolean -> value instanceof BooleanValue;

            default -> throw new UnsupportedOperationException("Unsupported scalar type: " + tokenType);
        };

        if (!(valid)) {
            throw new IllegalArgumentException("Value " + value + " does not match " + tokenType);
        }
    }

}
