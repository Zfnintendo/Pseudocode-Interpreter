package ast;

import java.util.List;

public interface ProcedureDeclaration extends Statement {
    String name();
    List<Parameter> parameters();
    List<Statement> body();
}
