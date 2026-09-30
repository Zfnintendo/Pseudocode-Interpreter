package ast;

import java.util.List;
import java.util.Optional;

public interface CaseStatement extends Statement {
    Identifier selector();
    List<Cases> cases();
    Optional<Statement> otherwiseCase();
}
