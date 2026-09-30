package ast;

import java.util.List;

public interface CallStatement extends Statement {
    CallExpression call();
}
