package ast;

public interface Input extends Statement {

    @Override
    NodeType kind();

    Assignable target();

}
