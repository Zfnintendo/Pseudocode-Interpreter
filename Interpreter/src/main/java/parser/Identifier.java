package parser;

import ast.NodeType;

public class Identifier extends ast.Identifier {

    public NodeType kind = NodeType.identifier;
    public String symbol;

    public Identifier(String value) {
        this.symbol = value;
    }

}
