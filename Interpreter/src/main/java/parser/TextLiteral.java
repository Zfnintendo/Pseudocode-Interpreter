package parser;

import ast.NodeType;
import ast.TextType;

public class TextLiteral implements ast.TextLiteral {

    private final String value;
    private final TextType type;

    public TextLiteral(String value, TextType type) {
        this.value = value;
        this.type = type;
    }

    @Override
    public NodeType kind() {
        return NodeType.textLiteral;
    }

    @Override
    public String value() {
        return this.value;
    }

    @Override
    public TextType type() {
        return this.type;
    }

}
