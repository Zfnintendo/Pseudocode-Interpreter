package parser;

import ast.Assignable;
import ast.Expression;
import ast.NodeType;

public record ReadFileStatement(Expression filename, Assignable target) implements ast.ReadFileStatement {

    @Override
    public NodeType kind() {
        return NodeType.readFileStatement;
    }
}
