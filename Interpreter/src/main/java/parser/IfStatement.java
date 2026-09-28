package parser;

import ast.Expression;
import ast.Statement;
import ast.NodeType;

import java.util.List;

public class IfStatement implements ast.IfStatement {

    private final Expression condition;
    private final List<Statement> thenBody;
    private final List<Statement> elseBody;

    public IfStatement(Expression condition, List<Statement> thenBody, List<Statement> elseBody) {
        this.condition = condition;
        this.thenBody = List.copyOf(thenBody);
        this.elseBody = List.copyOf(elseBody);
    }

    @Override
    public NodeType kind() {
        return NodeType.ifStatement;
    }

    @Override
    public Expression condition() {
        return this.condition;
    }

    @Override
    public List<Statement> thenBody() {
        return this.thenBody;
    }

    @Override
    public List<Statement> elseBody() {
        return this.elseBody;
    }


}
