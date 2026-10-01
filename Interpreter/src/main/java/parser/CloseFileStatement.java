package parser;

import ast.Expression;
import ast.NodeType;

public record CloseFileStatement(Expression filename) implements ast.CloseFileStatement {

    @Override
    public NodeType kind() {
        return NodeType.closeFileStatement;
    }
}
