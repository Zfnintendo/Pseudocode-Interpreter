package ast;

import lexer.TokenEnums;

public record ScalarType(TokenEnums tokenType) implements TypeSpecification {
}
