package ast;

public interface ReadFileStatement extends Statement {
    Expression filename();
    Assignable target();
}
