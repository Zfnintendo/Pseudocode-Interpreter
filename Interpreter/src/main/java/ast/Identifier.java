package ast;

public interface Identifier extends Expression{

    NodeType kind = NodeType.identifier;
    String symbol = null;


}
