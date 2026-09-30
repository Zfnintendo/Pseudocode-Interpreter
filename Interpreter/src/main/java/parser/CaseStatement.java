package parser;

import ast.Cases;
import ast.Statement;
import ast.Identifier;
import ast.NodeType;

import java.util.List;
import java.util.Optional;

public class CaseStatement implements ast.CaseStatement {

    private final Identifier selector;
    private final List<Cases> cases;
    private final Optional<Statement> otherwiseCase;

    public CaseStatement(Identifier selector, List<Cases> cases, Optional<Statement> otherwiseCase) {
        this.selector = selector;
        this.cases = List.copyOf(cases);
        this.otherwiseCase = otherwiseCase;
    }

    @Override
    public NodeType kind() {
        return NodeType.caseStatement;
    }

    @Override
    public Identifier selector() {
        return this.selector;
    }

    @Override
    public List<Cases> cases() {
        return this.cases;
    }

    @Override
    public Optional<Statement> otherwiseCase() {
        return this.otherwiseCase;
    }
}
