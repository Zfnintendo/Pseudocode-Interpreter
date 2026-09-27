package parser;

import ast.Expression;
import ast.NodeType;

import java.util.List;

public class Output implements ast.Output {

    private final List<Expression> values;

    public Output(List<Expression> values) {
        this.values = values;
    }

    @Override
    public NodeType kind() {
        return NodeType.output;
    }

    @Override
    public List<Expression> values() {
        return this.values;
    }

}
