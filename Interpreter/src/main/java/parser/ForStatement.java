package parser;

import ast.Expression;
import ast.NodeType;
import ast.Statement;
import java.util.List;

public class ForStatement implements ast.ForStatement {
    private final ast.Identifier variable;
    private final Expression start;
    private final Expression end;
    private final Expression step;
    private final List<Statement> body;

    public ForStatement(ast.Identifier variable, Expression start, Expression end, Expression step, List<Statement> body) {
        this.variable = variable;
        this.start = start;
        this.end = end;
        this.step = step;
        this.body = List.copyOf(body);
    }

    @Override
    public NodeType kind() {
        return NodeType.forStatement;
    }

    @Override
    public ast.Identifier variable() {
        return this.variable;
    }

    @Override
    public Expression start() {
        return this.start;
    }

    @Override
    public Expression end() {
        return this.end;
    }

    @Override
    public Expression step() {
        return this.step;
    }

    @Override
    public List<Statement> body() {
        return this.body;
    }
}