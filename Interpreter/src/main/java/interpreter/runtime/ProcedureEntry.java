package interpreter.runtime;

import ast.ProcedureDeclaration;

public record ProcedureEntry(ProcedureDeclaration declaration, Environment declarationEnvironment) {
}
