// ref https://youtu.be/aAvL2BTHf60?si=CxFZhF8YefAWDy3R

package parser;

import lexer.TokenEnums;
import lexer.TokenType;
import lexer.Token

import java.util.ArrayList;

public class MainParser {

    private ArrayList<ArrayList<Object>> tokens = new ArrayList<ArrayList<Object>>();
    public Program program;
    public ast.Statement statement;

    private boolean notEOF() {
        return this.tokens.getFirst().get(1) != TokenEnums.eof;
    }

    private ArrayList<Object> at() {
        return this.tokens.getFirst();
    }

    private ArrayList<Object> eat() {
        return this.tokens.removeFirst();
    }

    public Program produceAST(ArrayList<ArrayList<Object>> lexedFile) {

        this.tokens = lexedFile;
        this.program = new Program(this.tokens.size());

        // parse until end of file
        while (this.notEOF()) {
            this.program.body.add(this.parseStatement());
        }

        return null;


    }

    private ast.Statement parseStatement() {
        // skip to parse expression

        return this.parseExpression();
    }

    private ast.Expression parseExpression() {
        return this.parsePrimaryExpression();
    }

    private ast.Expression parsePrimaryExpression() {

        ArrayList<Object> tk = this.at();
        TokenType tkType = ((Token)tk.getFirst()).getType();
        String tkValue = ((Token)tk.getFirst()).getValue();

        switch(tkType) {
            case TokenType.identifier: {
                return new Identifier(tkValue);
            }

            default: {
                return new EmptyExpression();
            }
        }

    }


}
