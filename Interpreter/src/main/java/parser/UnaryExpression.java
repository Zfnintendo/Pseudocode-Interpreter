package parser;

import ast.Expression;
import ast.NodeType;

public class UnaryExpression implements ast.UnaryExpression {

    private final String operator;
    private final Expression operand;

    public UnaryExpression(String operator, Expression operand) {
        this.operator = operator;
        this.operand = operand;
    }

    @Override
    public NodeType kind() {
        return NodeType.unaryExpression;
    }

    @Override
    public String operator() {
        return this.operator();
    }

    @Override
    public Expression operand() {
        return this.operand;
    }

}
