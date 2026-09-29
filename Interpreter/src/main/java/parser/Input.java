package parser;

import ast.Assignable;
import ast.NodeType;

public class Input implements ast.Input{

    private final Assignable target;

    public Input(Assignable target) {
        this.target = target;
    }

    @Override
    public NodeType kind() {
        return NodeType.input;
    }

    @Override
    public Assignable target() {
        return this.target;
    }

}
