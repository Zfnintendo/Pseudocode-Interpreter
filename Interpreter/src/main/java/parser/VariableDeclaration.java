package parser;

import ast.NodeType;
import lexer.TokenEnums;

public class VariableDeclaration implements ast.VariableDeclaration {

    private final String name;
    private final TokenEnums type;

    public VariableDeclaration(String name, TokenEnums type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public NodeType kind() {
        return NodeType.variableDeclaration;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public TokenEnums declaredType() {
        return this.type;
    }

}
