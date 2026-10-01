package ast;

public interface OpenFileStatement extends Statement {
    Expression filename();
    FileMode mode();
}
