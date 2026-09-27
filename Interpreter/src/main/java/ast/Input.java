package ast;

public interface Input extends Statement {

    NodeType kind();
    Identifier identifier();

}
