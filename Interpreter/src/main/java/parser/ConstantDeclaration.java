package parser;

import ast.Expression;
import ast.NodeType;

public class ConstantDeclaration implements ast.ConstantDeclaration {

    private final String name;
    private final Expression value;

    public ConstantDeclaration(String name, Expression value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public NodeType kind() {
        return NodeType.constantDeclaration;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Expression value() {
        return this.value;
    }
}
