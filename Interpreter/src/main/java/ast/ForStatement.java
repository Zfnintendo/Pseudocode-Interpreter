package ast;

import java.util.List;

public interface ForStatement extends Statement {
    Identifier variable();
    Expression start();
    Expression end();
    Expression step();
    List<Statement> body();
}