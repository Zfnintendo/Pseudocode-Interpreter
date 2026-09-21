package ast;

import java.util.ArrayList;

public interface Program extends Statement{


    ArrayList<Statement> body();

    @Override
    NodeType kind();

}

