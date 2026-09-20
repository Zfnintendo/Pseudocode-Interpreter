package ast;

public class BinaryExpression extends Expression {

    public NodeType kind = NodeType.binaryExpression;

    public Expression LHS = null;
    public Expression RHS = null;
    public String operator = null;

}
