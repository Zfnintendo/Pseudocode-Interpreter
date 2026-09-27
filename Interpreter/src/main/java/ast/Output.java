package ast;

import java.util.List;

public interface Output extends Statement {

    @Override
    NodeType kind();

    List<Expression> values();

}
