package parser;

import ast.NodeType;
import ast.Expression;

public class BinaryExpression implements ast.BinaryExpression {

    public Expression left;
    public Expression right;
    public String op;

    public BinaryExpression(Expression LHS, Expression RHS, String operator) {
        this.left = LHS;
        this.right = RHS;
        this.op = operator;
    }

    @Override
    public NodeType kind() {
        return NodeType.binaryExpression;
    }

    @Override
    public Expression LHS() {
        return this.left;
    }

    @Override
    public Expression RHS() {
        return this.right;
    }

    @Override
    public String operator() {
        return this.op;
    }

}
