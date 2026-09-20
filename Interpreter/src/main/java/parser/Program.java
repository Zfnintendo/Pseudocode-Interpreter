package parser;

import ast.NodeType;
import ast.Statement;

import java.util.ArrayList;

public class Program extends ast.Program {

    public int size;

    public Program(int size) {
        this.size = size;
    }

    public NodeType kind = NodeType.program;
    public ArrayList<Statement> body = new ArrayList<Statement>();
}
