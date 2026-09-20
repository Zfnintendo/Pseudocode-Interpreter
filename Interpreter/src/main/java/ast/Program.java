package ast;

public interface Program extends Statement{

    NodeType kind = NodeType.program;
    Statement[] body = null;

}

