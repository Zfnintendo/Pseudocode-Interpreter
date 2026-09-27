package ast;

public interface Input extends Statement {

    @Override
    NodeType kind();

    Identifier identifier();

}
