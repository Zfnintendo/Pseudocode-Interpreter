package parser;

import ast.NodeType;

public class TextLiteral implements ast.TextLiteral {

    private final String value;

    public TextLiteral(String value) {
        this.value = value;
    }

    @Override
    public NodeType kind() {
        return NodeType.textLiteral;
    }

    @Override
    public String value() {
        return this.value;
    }

}
