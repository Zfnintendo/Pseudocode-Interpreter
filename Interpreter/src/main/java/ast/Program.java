package ast;

public interface Program extends Statement{

    NodeType kind = NodeType.program;// shadowing
    Statement[] body = null;

}

