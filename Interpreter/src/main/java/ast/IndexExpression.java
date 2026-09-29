package ast;

import java.util.List;

public interface IndexExpression extends Assignable {

    Assignable base();
    List<Expression> indices();

}
