package parser;

import ast.NodeType;
import ast.TypeSpecification;

public class VariableDeclaration implements ast.VariableDeclaration {

    private final String name;
    private final TypeSpecification type;

    public VariableDeclaration(String name, TypeSpecification type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public NodeType kind() {
        return NodeType.variableDeclaration;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public TypeSpecification declaredType() {
        return this.type;
    }

}
