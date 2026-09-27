package parser;

import ast.Expression;
import ast.NodeType;

public class Assignment implements ast.Assignment {

    private final Identifier target;
    private final Expression value;

    public Assignment(Identifier target, Expression value) {
        this.target = target;
        this.value = value;
    }

    @Override
    public NodeType kind() {
        return NodeType.assignment;
    }

    @Override
    public Identifier target() {
        return this.target;
    }

    @Override
    public Expression value() {
        return this.value;
    }

}
