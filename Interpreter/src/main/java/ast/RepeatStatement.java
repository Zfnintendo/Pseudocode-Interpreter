package ast;

import java.util.List;

public interface RepeatStatement extends Statement {
    List<Statement> body();
    Expression condition();
}
