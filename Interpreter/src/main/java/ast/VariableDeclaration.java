package ast;

import lexer.TokenEnums;

public interface VariableDeclaration extends Statement {

    NodeType kind();

    String name();
    TypeSpecification declaredType();

}
