package ast;

public interface Assignment extends Statement {

    @Override
    NodeType kind();

    Assignable target();
    Expression value();

}
