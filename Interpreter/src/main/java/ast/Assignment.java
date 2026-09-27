package ast;

public interface Assignment extends Statement {

    NodeType kind();

    Identifier target();
    Expression value();

}
