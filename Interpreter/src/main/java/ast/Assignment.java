package ast;

public interface Assignment extends Statement {

    @Override
    NodeType kind();

    Identifier target();
    Expression value();

}
