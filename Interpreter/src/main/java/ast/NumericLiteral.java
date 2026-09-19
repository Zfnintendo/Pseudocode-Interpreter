package ast;

public interface NumericLiteral extends Expression {

    NodeType kind = NodeType.numericLiteral;
    double value = 0;

}
