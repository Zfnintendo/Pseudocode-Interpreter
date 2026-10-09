package interpreter.runtime;

import ast.TypeSpecification;
import interpreter.values.RuntimeValue;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private final Map<String, VariableEntry> variables = new HashMap<>();

    private String key(String name) {
        return name;
    }

    public void declareVariable(String name, TypeSpecification type) {
        String key = this.key(name);

        if (this.variables.containsKey(key)) {
            throw new IllegalArgumentException("Name already declared: " + name);
        }

        this.variables.put(key, new VariableEntry(type));
    }

    public void declareConstant(String name, TypeSpecification type, RuntimeValue value) {
        String key = this.key(name);

        if (this.variables.containsKey(key)) {
            throw new IllegalArgumentException("Name already declared: " + name);
        }

        this.variables.put(key, new VariableEntry(type, value, true));
    }

    private VariableEntry lookup(String name) {
        VariableEntry entry = this.variables.get(this.key(name));

        if (entry == null) {
            throw new IllegalArgumentException("Name was not declared: " + name);
        }

        return entry;
    }

    public RuntimeValue read(String name) {
        VariableEntry entry = this.lookup(name);

        if(!entry.isInitialised()) {
            throw new IllegalStateException("Variable has not been assigned a value: " + name);
        }

        return entry.value();
    }

    public void assign(String name, RuntimeValue value) {
        VariableEntry entry = this.lookup(name);

        if (entry.isConstant()) {
            throw new IllegalStateException("Can not reassign a constant: " + name);
        }

        entry.assign(value);
    }

    public TypeSpecification declaredType(String name) {
        return this.lookup(name).declaredType();
    }

}