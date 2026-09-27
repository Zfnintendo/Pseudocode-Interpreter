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

    // eats the token and checks for the type if not expected then return error
    private ArrayList<Object> expect(TokenEnums tokenType, String err) {
        ArrayList<Object> token = this.tokens.removeFirst();
        TokenEnums temp = (TokenEnums) token.get(1);
        if ( temp != tokenType ) {
            throw new NoSuchElementException(err + "| Got: " + token + "| Expected: " + tokenType);
        }

        return token;
    }

    private String currentValue() {
        return ((Token) this.at().getFirst()).getValue();
    }

    private TokenEnums currentType() {
        return (TokenEnums) this.at().get(1);
    }

    public Program produceAST(ArrayList<ArrayList<Object>> lexedFile) {

        this.tokens = lexedFile;
        this.program = new Program(this.tokens.size());

        // parse until end of file
        while (this.notEOF()) {
            // Skip blank lines or the newline after a statement.
            if (this.at().get(1) == TokenEnums.eol) {
                this.eat();
                continue;
            }

            this.program.body().add(this.parseStatement());

            // A complete statement must end at a newline or EOF.
            if (this.at().get(1) != TokenEnums.eol && this.at().get(1) != TokenEnums.eof) {
                throw new IllegalArgumentException("Expected end of line after statement, got: " + this.at());
            }
        }

        return this.program;


    }

    private ast.Statement parseStatement() {
        return switch(this.currentType()) {
            case TokenEnums.declare -> this.parseVariableDeclaration();
            case TokenEnums.identifier -> this.parseAssignment();
            case TokenEnums.output -> this.parseOutput();
            case TokenEnums.input -> this.parseInput();

            default -> throw new IllegalArgumentException("Expected a statement, got: " + this.currentType());
        };
    }

    private ast.Input parseInput() {
        this.expect(TokenEnums.input, "Expected INPUT");

        ArrayList<Object> tokenObject = this.expect(TokenEnums.identifier, "Expected a variable name.");
        String name = ((Token) tokenObject.getFirst()).getValue();

        return new Input(new Identifier(name));
    }

    private ast.Output parseOutput() {
        this.expect(TokenEnums.output, "Expected OUTPUT.");

        ArrayList<ast.Expression> values = new ArrayList<>();

        values.add(this.parseExpression());

        while (this.currentType() == TokenEnums.comma || this.currentType() == TokenEnums.concatenate) {
            this.eat();
            values.add(this.parseExpression());
        }

        return new Output(values);
    }

    private ast.VariableDeclaration parseVariableDeclaration() {
        this.expect(TokenEnums.declare, "Expected DECLARE.");

        ArrayList<Object> tokenObject = this.expect(TokenEnums.identifier, "Expected variable name after DECLARE.");

        String name = ((Token) tokenObject.getFirst()).getValue();

        this.expect(TokenEnums.colon, "Expected ':' after variable name.");

        TokenEnums type = this.currentType();

        switch (type) {
            case TokenEnums.typeInteger, TokenEnums.typeReal, TokenEnums.typeChar, TokenEnums.typeString, TokenEnums.typeBoolean: {
                this.eat();
                break;
            }
            default: {
                throw new IllegalArgumentException("Expected data type after ':', got: " + type);
            }

        }

        return new VariableDeclaration(name, type);
    }

    private ast.Assignment parseAssignment() {
        ArrayList<Object> tokenObject = this.expect(TokenEnums.identifier, "Expected assignment variable.");

        String name = ((Token) tokenObject.getFirst()).getValue();

        this.expect(TokenEnums.assignment, "Expected '<-' after assignment variable");

        ast.Expression value = this.parseExpression();

        return new Assignment(new Identifier(name), value);
    }


    // expressions start here
    private ast.Expression parseExpression() {
        return this.parseAdditiveExpression();
    }

    private ast.Expression parseAdditiveExpression() {
        ast.Expression left = this.parseMultiplicativeExpression();

        String tkValue = this.currentValue(); // gets token value at

        while ( tkValue.equals("+") || tkValue.equals("-") ){
            String operator = ((Token)this.eat().getFirst()).getValue();
            ast.Expression right = this.parseMultiplicativeExpression();

            left = new BinaryExpression(left, right, operator);

            tkValue = this.currentValue();

        }

        return left;
    }

    private ast.Expression parseMultiplicativeExpression() {
        ast.Expression left = this.parsePrimaryExpression();

        String tkValue = this.currentValue();

        while ( tkValue.equals("*") || tkValue.equals("/") || tkValue.equals("MOD") ||tkValue.equals("DIV") ) {
            final String operator = ((Token)this.eat().getFirst()).getValue();
            final ast.Expression right = this.parsePrimaryExpression();

            left = new BinaryExpression(left, right, operator);

            tkValue = this.currentValue();

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
