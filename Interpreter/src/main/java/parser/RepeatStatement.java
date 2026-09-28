package parser;

import ast.Expression;
import ast.NodeType;
import ast.Statement;

import java.util.List;

public class RepeatStatement implements ast.RepeatStatement {

    private final List<Statement> body;
    private final Expression condition;

    public RepeatStatement(List<Statement> body, Expression condition) {
        this.body = List.copyOf(body);
        this.condition = condition;
    }

    @Override
    public NodeType kind() {
        return NodeType.repeatStatement;
    }

    @Override
    public List<Statement> body() {
        return this.body;
    }

    @Override
    public Expression condition() {
        return this.condition();
    }
}
