package parser;

import ast.NodeType;

public class BooleanLiteral implements ast.BooleanLiteral {

    private final boolean value;

    public BooleanLiteral(boolean value) {
        this.value = value;
    }

    @Override
    public NodeType kind() {
        return NodeType.booleanLiteral;
    }

    @Override
    public boolean value() {
        return this.value;
    }

}
