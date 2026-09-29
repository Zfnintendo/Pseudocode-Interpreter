package parser;

import ast.Assignable;
import ast.Expression;
import ast.NodeType;

public class Assignment implements ast.Assignment {

    private final Assignable target;
    private final Expression value;

    public Assignment(Assignable target, Expression value) {
        this.target = target;
        this.value = value;
    }

    @Override
    public NodeType kind() {
        return NodeType.assignment;
    }

    @Override
    public Assignable target() {
        return this.target;
    }

    @Override
    public Expression value() {
        return this.value;
    }

}
