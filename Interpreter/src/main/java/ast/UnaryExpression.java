package ast;

public interface UnaryExpression extends Expression {
    String operator();
    Expression operand();
}
