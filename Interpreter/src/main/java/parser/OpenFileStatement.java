package parser;

import ast.Expression;
import ast.FileMode;
import ast.NodeType;

public record OpenFileStatement(Expression filename, FileMode mode) implements ast.OpenFileStatement {

    @Override
    public NodeType kind() {
        return NodeType.openFileStatement;
    }
}
