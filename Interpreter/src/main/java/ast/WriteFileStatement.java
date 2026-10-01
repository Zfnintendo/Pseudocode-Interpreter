package ast;

public interface WriteFileStatement extends Statement {
    Expression filename();
    Expression value();
}
