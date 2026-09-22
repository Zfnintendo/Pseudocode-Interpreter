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
import java.util.NoSuchElementException;

public class MainParser {

    private ArrayList<ArrayList<Object>> tokens = new ArrayList<ArrayList<Object>>();
    public Program program;

    private boolean notEOF() {
        return this.tokens.getFirst().get(1) != TokenEnums.eof;
    }

    // gets the first tokenObject in the list
    private ArrayList<Object> at() {
        return this.tokens.getFirst();
    }

    // returns the first token after eating
    private ArrayList<Object> eat() {
        ArrayList<Object> token = this.tokens.removeFirst();
        return token;
    }

    private ArrayList<Object> expect(TokenEnums tokenType, String err) {
        ArrayList<Object> token = this.tokens.removeFirst();
        TokenEnums temp = (TokenEnums) token.get(1);
        if ( temp != tokenType ) {
            throw new NoSuchElementException(err + "| Got: " + token + "| Expected: " + tokenType);
        }

        return token;
    }

    public Program produceAST(ArrayList<ArrayList<Object>> lexedFile) {

        this.tokens = lexedFile;
        this.program = new Program(this.tokens.size());

        // parse until end of file
        while (this.notEOF()) {
            this.program.body().add(this.parseStatement());
        }

        return this.program;


    }

    private ast.Statement parseStatement() {
        // skip to parse expression

        return this.parseExpression();
    }

    private ast.Expression parseExpression() {
        return this.parseAdditiveExpression();
    }

    private ast.Expression parseAdditiveExpression() {
        ast.Expression left = this.parseMultiplicativeExpression();

        String tkValue = ((Token)this.at().getFirst()).getValue(); // gets token value at

        while ( tkValue == "+" || tkValue == "-" ) {
            final String operator = ((Token)this.eat().getFirst()).getValue();
            final ast.Expression right = this.parseMultiplicativeExpression();

            left = new BinaryExpression(left, right, operator);

        }

        return left;
    }

    private ast.Expression parseMultiplicativeExpression() {
        ast.Expression left = this.parsePrimaryExpression();

        String tkValue = ((Token)this.at().getFirst()).getValue();

        while ( tkValue == "*" || tkValue == "/" || tkValue == "MOD" || tkValue == "DIV") {
            final String operator = ((Token)this.eat().getFirst()).getValue();
            final ast.Expression right = this.parsePrimaryExpression();

            left = new BinaryExpression(left, right, operator);

        }

        return left;
    }

    private ast.Expression parsePrimaryExpression() {

        ArrayList<Object> tk = this.at();
        TokenEnums tkType = ( (TokenEnums) tk.get(1));
        String tkValue = ((Token)this.eat().getFirst()).getValue(); // gets the eaten value

        switch(tkType) {
            case TokenEnums.identifier: {
                return new Identifier(tkValue);
            }
            case TokenEnums.literalInteger, TokenEnums.literalReal: {
                return new NumericLiteral(tkValue);
            }
            case TokenEnums.leftParen: {
                this.eat();
                final ast.Expression value = this.parseExpression();
                this.expect(TokenEnums.rightParen, "Unexpected token found inside parenthesised expression. Expected closing parenthesis.");
                return value;
            }
            default: {
                throw new UnknownError("Unexpected token found during parsing!");
            }
        }

    }


}
