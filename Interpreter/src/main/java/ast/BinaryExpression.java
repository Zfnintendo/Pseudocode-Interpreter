package ast;

public interface BinaryExpression extends Expression {

    NodeType kind = NodeType.binaryExpression;

    Expression LHS = null;
    Expression RHS = null;
    String operator = null;

}
