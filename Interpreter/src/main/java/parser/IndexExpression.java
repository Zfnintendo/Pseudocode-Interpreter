package parser;

import ast.Assignable;
import ast.Expression;
import ast.NodeType;
import java.util.List;

public class IndexExpression implements ast.IndexExpression {

    private final Assignable base;
    private final List<Expression> indices;

    public IndexExpression(Assignable base, List<Expression> indices) {
        this.base = base;
        this.indices = List.copyOf(indices);
    }

    @Override
    public NodeType kind() {
        return NodeType.indexExpression;
    }

    @Override
    public Assignable base() {
        return this.base();
    }

    @Override
    public List<Expression> indices() {
        return this.indices;
    }

}
