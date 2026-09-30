package ast;

public interface ConstantDeclaration extends Statement {
    String name();
    Expression value();
}
