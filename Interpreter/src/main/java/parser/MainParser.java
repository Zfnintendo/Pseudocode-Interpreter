// ref https://youtu.be/aAvL2BTHf60?si=CxFZhF8YefAWDy3R

// goddamn i really messed up my intial structure now bear the pain with spagetti code

// reminder on what all these things do oh my god
// tokens.get() gets i tokenObject in the list
// tokens.removeFirst() removes the first thing in the list and shifts
// tokens.getFirst().get(1) means gets the token enum
// tokens.getFirst().getFirst() returns token and its details

package parser;

import ast.NodeType;
import lexer.TokenEnums;
import lexer.TokenType;
import lexer.Token;

import java.util.ArrayList;

public class MainParser {

    private ArrayList<ArrayList<Object>> tokens = new ArrayList<ArrayList<Object>>();
    public Program program;
    public ast.Statement statement;



    private boolean notEOF() {
        return this.tokens.getFirst().get(1) != TokenEnums.eof;
    }

    // gets the first tokenObject in the list
    private ArrayList<Object> at() {
        return this.tokens.getFirst();
    }

    private ArrayList<ArrayList<Object>> eat() {
        this.tokens.removeFirst();
        return this.tokens;
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
        TokenEnums tkType = ( (TokenEnums) tk.get(1));
        String tkValue = ((Token)this.eat().getFirst().getFirst()).getValue(); // gets token value

        switch(tkType) {
            case TokenEnums.identifier: {
                return new Identifier(tkValue);
            }
            case TokenEnums.literalInteger, TokenEnums.literalReal: {
                return new NumericLiteral(tkValue);
            }
            default: {
                throw new UnknownError("Unexpected token found during parsing!");
            }
        }

    }


}
