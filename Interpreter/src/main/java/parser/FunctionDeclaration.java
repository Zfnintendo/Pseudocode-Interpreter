package parser;

import ast.NodeType;
import ast.Parameter;
import ast.Statement;
import ast.TypeSpecification;

import java.util.List;

public class FunctionDeclaration implements ast.FunctionDeclaration {

    private final String name;
    private final List<Parameter> parameters;
    private final TypeSpecification returnType;
    private final List<Statement> body;

    public FunctionDeclaration(String name, List<Parameter> parameters, TypeSpecification returnType, List<Statement> body) {
        this.name = name;
        this.parameters = List.copyOf(parameters);
        this.returnType = returnType;
        this.body = List.copyOf(body);
    }

    @Override
    public NodeType kind() {
        return NodeType.functionDeclaration;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public List<Parameter> parameters() {
        return this.parameters;
    }

    @Override
    public TypeSpecification returnType() {
        return this.returnType;
    }

    @Override
    public List<Statement> body() {
        return this.body;
    }

}
