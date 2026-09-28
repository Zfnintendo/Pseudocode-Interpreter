package ast;

public interface TextLiteral extends Expression {

    @Override
    NodeType kind();

    String value();

    TextType type();

}
