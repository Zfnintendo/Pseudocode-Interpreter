package ast;

import java.util.List;

public interface Output extends Statement {

    NodeType kind();
    List<Expression> values();

}
