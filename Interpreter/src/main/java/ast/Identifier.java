package ast;

public interface Identifier extends Assignable{

    @Override
    NodeType kind(); // identifier

    String symbol();


}
