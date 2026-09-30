package parser;

import ast.NodeType;
import ast.Parameter;
import ast.Statement;

import java.util.List;

public class ProcedureDeclaration implements ast.ProcedureDeclaration {

    private final String name;
    private final List<Parameter> parameters;
    private final List<Statement> body;

    public ProcedureDeclaration(String name, List<Parameter> parameters, List<Statement> body) {
        this.name = name;
        this.parameters = List.copyOf(parameters);
        this.body = List.copyOf(body);
    }

    @Override
    public NodeType kind() {
        return NodeType.procedureDeclaration;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public List<Parameter> parameters() {
        return this.parameters;
    }

    @Override
    public List<Statement> body() {
        return this.body;
    }

}
