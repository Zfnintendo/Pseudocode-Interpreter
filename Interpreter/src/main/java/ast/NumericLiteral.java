package ast;

public interface NumericLiteral extends Expression {

    NodeType kind = NodeType.numericLiteral;
    String value = null;

}
