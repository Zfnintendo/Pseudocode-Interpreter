package ast;

import java.util.List;

public interface CallExpression extends Expression {
    String name();
    List<Expression> arguments();
}
