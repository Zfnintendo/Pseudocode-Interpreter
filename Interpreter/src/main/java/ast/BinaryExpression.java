package ast;

public interface BinaryExpression extends Expression {

    @Override
    NodeType kind(); // binaryExpression

    Expression LHS();
    Expression RHS();
    String operator();

}
