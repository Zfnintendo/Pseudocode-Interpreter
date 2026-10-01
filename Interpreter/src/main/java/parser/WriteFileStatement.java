package parser;

import ast.Expression;
import ast.NodeType;

public record WriteFileStatement(Expression filename, Expression value) implements ast.WriteFileStatement {

    @Override
    public NodeType kind() {
        return NodeType.writeFileStatement;
    }
}
