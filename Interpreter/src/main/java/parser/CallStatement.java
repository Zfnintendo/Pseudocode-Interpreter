package parser;

import ast.CallExpression;
import ast.NodeType;

public class CallStatement implements ast.CallStatement {

    private final CallExpression call;

    public CallStatement(CallExpression call) {
        this.call = call;
    }

    @Override
    public NodeType kind() {
        return NodeType.callStatement;
    }

    @Override
    public CallExpression call() {
        return this.call;
    }

}
