package interpreter.runtime;

import ast.TypeSpecification;
import ast.FunctionDeclaration;
import ast.ProcedureDeclaration;
import interpreter.values.RuntimeValue;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private final Map<String, VariableEntry> variables = new HashMap<>();
    private final Map<String, FunctionEntry> functions = new HashMap<>();
    private final Map<String, ProcedureEntry>  procedures = new HashMap<>();

    private final FileManager files;

    private final Environment parent;

    public Environment() {
        this(null);
    }

    public Environment(Environment parent) {
        this.parent = parent;
        this.files = parent == null ? new FileManager() : parent.files;
    }

    private String key(String name) {
        return name;
    }

    public FileManager files() {
        return files;
    }

    public void declareVariable(String name, TypeSpecification type) {
        String key = this.key(name);

        if (this.variables.containsKey(key)) {
            throw new IllegalArgumentException("Name already declared: " + name);
        }

        this.variables.put(key, new VariableEntry(type));
    }

    public void declareVariable(String name, TypeSpecification type, RuntimeValue initialValue) {
        String key = this.key(name);

        if (this.variables.containsKey(key)) {
            throw new IllegalArgumentException("Name already declared: " + name);
        }

        this.variables.put(key, new VariableEntry(type, initialValue, false));
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

        if (entry != null) {
            return entry;
        }

        if (this.parent != null) {
            return this.parent.lookup(name);
        }

        throw new IllegalArgumentException("Name was not declared: " + name);
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

    public void declareFunction(FunctionDeclaration declaration) {
        checkSubroutineName(declaration.name());

        functions.put(this.key(declaration.name()), new FunctionEntry(declaration, this));
    }

    public void declareProcedure(ProcedureDeclaration declaration) {
        checkSubroutineName(declaration.name());

        procedures.put(this.key(declaration.name()), new ProcedureEntry(declaration, this));
    }

    public FunctionEntry lookupFunction(String name) {
        String key = this.key(name);
        FunctionEntry entry = functions.get(key);

        if (entry != null) {
            return entry;
        }

        if (procedures.containsKey(key)) {
            throw new IllegalArgumentException("Expected a function, but found a procedure: " + name);
        }

        if (parent != null) {
            return parent.lookupFunction(name);
        }

        throw new IllegalArgumentException("Function was not declared: " + name);
    }

    public ProcedureEntry lookupProcedure(String name) {
        String key = this.key(name);
        ProcedureEntry entry = procedures.get(key);

        if (entry != null) {
            return entry;
        }

        if (functions.containsKey(key)) {
            throw new IllegalArgumentException("Expected a procedure, but found a function: " + name);
        }

        if (parent != null) {
            return parent.lookupProcedure(name);
        }

        throw new IllegalArgumentException("Procedure was not declared: " + name);
    }

    private void checkSubroutineName(String name) {
        String key = this.key(name);

        if (functions.containsKey(key)  || procedures.containsKey(key)) {
            throw new IllegalArgumentException("Subroutine already declared: " + name);
        }
    }

}