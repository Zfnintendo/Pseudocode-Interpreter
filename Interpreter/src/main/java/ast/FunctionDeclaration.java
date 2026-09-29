package ast;

import java.util.List;

public interface FunctionDeclaration extends Statement {

    String name();
    List<Parameter> parameters();
    TypeSpecification returnType();
    List<Statement> body();

}
