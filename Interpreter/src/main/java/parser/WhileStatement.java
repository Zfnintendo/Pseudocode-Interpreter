package parser;

import ast.Expression;
import ast.NodeType;
import ast.Statement;

import java.util.List;

public class WhileStatement implements ast.WhileStatement {

    private final Expression condition;
    private final List<Statement> body;

    public WhileStatement(Expression condition, List<Statement> body) {
        this.condition = condition;
        this.body = List.copyOf(body);
    }

    @Override
    public NodeType kind() {
        return NodeType.whileStatement;
    }

    @Override
    public Expression condition() {
        return this.condition;
    }

    @Override
    public List<Statement> body() {
        return this.body;
    }
}
