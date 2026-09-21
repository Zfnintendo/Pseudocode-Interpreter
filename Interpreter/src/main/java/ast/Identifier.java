package ast;

public interface Identifier extends Expression{

    @Override
    NodeType kind(); // identifier

    String symbol();


}
