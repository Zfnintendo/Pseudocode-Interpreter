package ast;

import java.util.List;

public interface IfStatement extends Statement {

    Expression condition();
    List<Statement> thenBody();
    List<Statement> elseBody();

}
