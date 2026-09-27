package parser;

import ast.Identifier;
import ast.NodeType;

public class Input implements ast.Input{

    private final Identifier identifier;

    public Input(Identifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public NodeType kind() {
        return NodeType.input;
    }

    @Override
    public Identifier identifier() {
        return this.identifier;
    }

}
