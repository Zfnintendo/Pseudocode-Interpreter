package parser;

import ast.NodeType;

public class Identifier implements ast.Identifier {

    public String sym;

    public Identifier(String value) {
        this.sym = value;
    }

    @Override
    public NodeType kind() {
        return NodeType.identifier;
    }

    @Override
    public String symbol() {
        return this.sym;
    }

}
