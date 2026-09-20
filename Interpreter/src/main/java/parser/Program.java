package parser;

import ast.NodeType;
import ast.Statement;

import java.util.ArrayList;

public class Program implements ast.Program {

    public int size;

    public Program(int size) {
        this.size = size;
    }

    public ArrayList<Statement> body = new ArrayList<Statement>();
}
