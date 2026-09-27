package ast;

public interface BooleanLiteral extends Expression {

    // it was only until here i realised that i was inheriting the kind the whole time and did not need to overwrite it everytime LOL
    // I should stop following tutorials in another language
    boolean value();
}
