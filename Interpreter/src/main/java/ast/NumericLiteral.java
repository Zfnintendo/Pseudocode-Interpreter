package ast;

public interface NumericLiteral extends Expression {

    @Override
    NodeType kind(); // NumericLiteral

    String value();

}
