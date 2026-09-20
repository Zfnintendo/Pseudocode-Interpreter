package ast;

public class Program extends Statement{

    public NodeType kind = NodeType.program;
    public Statement[] body;

}

