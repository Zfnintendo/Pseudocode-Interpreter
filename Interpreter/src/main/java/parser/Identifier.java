package parser;

import ast.NodeType;

public class Identifier implements ast.Identifier {

    public String symbol;

    public Identifier(String value) {
        this.symbol = value;
    }

}
