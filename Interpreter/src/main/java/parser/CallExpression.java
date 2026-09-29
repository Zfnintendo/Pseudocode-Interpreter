package parser;

import ast.Expression;
import ast.NodeType;

import java.util.List;

public class CallExpression implements ast.CallExpression {

    private final String name;
    private final List<Expression> arguments;

    public CallExpression(String name, List<Expression> args) {
        this.name = name;
        this.arguments = List.copyOf(args);
    }

    @Override
    public NodeType kind() {
        return NodeType.callExpression;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public List<Expression> arguments() {
        return this.arguments;
    }
}
