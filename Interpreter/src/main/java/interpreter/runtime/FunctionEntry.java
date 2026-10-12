package interpreter.runtime;

import ast.FunctionDeclaration;

public record FunctionEntry(FunctionDeclaration declaration, Environment declarationEnvironment) {
}
