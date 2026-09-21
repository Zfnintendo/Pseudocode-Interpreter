package parser;

import ast.NodeType;

public class NumericLiteral implements ast.NumericLiteral {

    public String val;

    public NumericLiteral(String value) {
        this.val = value;
    }
    @Override
    public NodeType kind() {
        return NodeType.numericLiteral;
    }

    @Override
    public String value() {
        return this.val;
    }
}


