package ast;

import java.util.List;

public interface WhileStatement extends Statement {
    Expression condition();
    List<Statement> body();
}
