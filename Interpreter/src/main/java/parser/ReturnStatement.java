package parser;

import ast.Expression;
import ast.NodeType;

public class ReturnStatement implements ast.ReturnStatement{
    private final Expression value;

    public ReturnStatement(Expression value) {
        this.value = value;
    }

    @Override
    public NodeType kind() {
        return NodeType.returnStatement;
    }

    @Override
    public Expression value() {
        return this.value;
    }
}
