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
import java.util.Set;
import java.util.Optional;

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

    private boolean isComparisonOperator() {
        return switch (this.currentType()) {
            case TokenEnums.equals, TokenEnums.notEqualTo, TokenEnums.lessThan, TokenEnums.greaterThan,
                 TokenEnums.lessEqualTo, TokenEnums.greaterEqualTo -> true;
            default -> false;
        };
    }



    // main method
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
    // main method



    private ast.Statement parseStatement() {
        return switch(this.currentType()) {
            case TokenEnums.declare -> this.parseVariableDeclaration();
            case TokenEnums.constant -> this.parseConstantDeclaration();
            case TokenEnums.identifier -> this.parseAssignment();

            case TokenEnums.output -> this.parseOutput();
            case TokenEnums.input -> this.parseInput();

            case TokenEnums.tokenIf -> this.parseIfStatement();
            case TokenEnums.tokenWhile -> this.parseWhileStatement();
            case TokenEnums.repeat -> this.parseRepeatStatement();
            case TokenEnums.tokenFor -> this.parseForStatement();
            case TokenEnums.tokenCase -> this.parseCaseStatement();

            case TokenEnums.function -> this.parseFunctionDeclaration();
            case TokenEnums.tokenReturn -> this.parseReturnStatement();
            case TokenEnums.procedure -> this.parseProcedureDeclaration();
            case TokenEnums.call -> this.parseCallStatement();

            case TokenEnums.openFile -> this.parseOpenFileStatement();
            case TokenEnums.readFile -> this.parseReadFileStatement();
            case TokenEnums.writeFile -> this.parseWriteFileStatement();
            case TokenEnums.closeFile -> this.parseCloseFileStatement();

            default -> throw new IllegalArgumentException("Expected a statement, got: " + this.currentType());
        };
    }

    private ast.IfStatement parseIfStatement() {
        this.expect(TokenEnums.tokenIf, "Expected IF");

        ast.Expression condition = this.parseExpression();

        if (this.currentType() == TokenEnums.eol) {
            this.eat();
        }

        this.expect(TokenEnums.then, "Expected THEN after the IF condition.");

        this.expect(TokenEnums.eol, "Expected a newline after THEN.");

        ArrayList<ast.Statement> thenBody = this.parseBlock(Set.of(TokenEnums.tokenElse, TokenEnums.endIf));
        ArrayList<ast.Statement> elseBody = new ArrayList<>();

        if (this.currentType() == TokenEnums.tokenElse) {
            this.eat();
            this.expect(TokenEnums.eol, "Expected a new line after else.");

            elseBody = this.parseBlock(Set.of(TokenEnums.endIf));
        }

        this.expect(TokenEnums.endIf, "Expected ENDIF");

        return new IfStatement(condition, thenBody, elseBody);
    }

    private ast.WhileStatement parseWhileStatement() {
        this.expect(TokenEnums.tokenWhile, "Expected WHILE");

        ast.Expression condition = this.parseExpression();
        this.expect(TokenEnums.eol, "Expected a new line after WHILE condition.");

        ArrayList<ast.Statement> body = this.parseBlock(Set.of(TokenEnums.endWhile));

        this.expect(TokenEnums.endWhile, "Expected an ENDWHILE");

        return new WhileStatement(condition, body);
    }

    private ast.RepeatStatement parseRepeatStatement() {
        this.expect(TokenEnums.repeat, "Expected REPEAT.");
        this.expect(TokenEnums.eol, "Expected a newline after REPEAT");

        ArrayList<ast.Statement> body = this.parseBlock(Set.of(TokenEnums.until));

        this.expect(TokenEnums.until, "Expected UNTIL.");
        ast.Expression condition = this.parseExpression();

        return new RepeatStatement(body, condition);
    }

    private ast.ForStatement parseForStatement() {
        this.expect(TokenEnums.tokenFor, "Expected FOR");

        ArrayList<Object> variableObject = this.expect(TokenEnums.identifier, "Expected an identifier after FOR.");

        String variableName = ((Token) variableObject.getFirst()).getValue();

        this.expect(TokenEnums.assignment, "Expected '<-' after FOR variable.");

        ast.Expression start = this.parseExpression();

        this.expect(TokenEnums.tokenTo, "Expected TO after inital value.");

        ast.Expression end = this.parseExpression();
        ast.Expression step = new NumericLiteral("1");

        if (this.currentType() == TokenEnums.step) {
            this.eat();
            step = this.parseExpression();
        }

        this.expect(TokenEnums.eol, "Expected a new line after FOR statement.");

        ArrayList<ast.Statement> body = this.parseBlock(Set.of(TokenEnums.next));

        this.expect(TokenEnums.next, "Expected NEXT.");

        ArrayList<Object> closingToken = this.expect(TokenEnums.identifier, "Expected FOR loop variable after NEXT");

        String closingName = ((Token) closingToken.getFirst()).getValue();

        if (!variableName.equals(closingName)) { throw new IllegalArgumentException("Expected NEXT " + variableName + ", got NEXT " + closingName); }

        return new ForStatement(new Identifier(variableName), start, end, step, body);
    }

    private ast.ReturnStatement parseReturnStatement() {
        this.expect(TokenEnums.tokenReturn, "Expected RETURN");

        ast.Expression value = this.parseExpression();

        return new ReturnStatement(value);
    }

    private ast.CallStatement parseCallStatement() {
        this.expect(TokenEnums.call, "Expected CALL.");

        ArrayList<Object> tokenObject = this.expect(TokenEnums.identifier, "Expected procedure name after CALL.");

        String name = ((Token) tokenObject.getFirst()).getValue();

        ArrayList<ast.Expression> arguments = new ArrayList<>();

        if (this.currentType() == TokenEnums.leftParen) {
            arguments = this.parseArguments();
        }

        return new CallStatement(new CallExpression(name, arguments));
    }

    private ast.CaseStatement parseCaseStatement() {
        this.expect(TokenEnums.tokenCase, "Expected CASE.");
        this.expect(TokenEnums.of, "Expected OF after CASE.");

        ArrayList<Object> selectorTokenObject = this.expect(TokenEnums.identifier, "Expected selector identifier after CASE OF.");

        String name = ((Token) selectorTokenObject.getFirst()).getValue();

        this.expect(TokenEnums.eol, "Expected a new line after CASE OF <IDENTIFIER>.");

        ArrayList<ast.Cases> cases = new ArrayList<>();
        Optional<ast.Statement> otherwiseCase = Optional.empty();

        while (true) {
            while (this.currentType() == TokenEnums.eol) {
                this.eat();
            }

            if (this.currentType() == TokenEnums.endCase) {
                break;
            }

            if (this.currentType() == TokenEnums.eof) {
                throw new IllegalArgumentException("Unexpected end of file. Expected ENDCASE.");
            }

            if (otherwiseCase.isPresent()) {
                throw new IllegalArgumentException("OTHERWISE must be the final CASE case.");
            }

            if (this.currentType() == TokenEnums.otherwise) {
                this.eat();

                ast.Statement statement = this.parseStatement();
                otherwiseCase = Optional.of(statement);
            } else {
                ast.Expression value = this.parseConstantLiteral();

                this.expect(TokenEnums.colon, "Expected ':' after CASE value.");

                ast.Statement statement = this.parseStatement();

                cases.add(new ast.Cases(value, statement));
            }

            this.expect(TokenEnums.eol, "Expected a new line after CASE case.");
        }

        if (cases.isEmpty()) {
            throw new IllegalArgumentException("CASE requires atleast one case branch.");
        }

        this.expect(TokenEnums.endCase, "Expected ENDCASE.");

        return new CaseStatement(new Identifier(name), cases, otherwiseCase);
    }

    private ast.OpenFileStatement parseOpenFileStatement() {
        this.expect(TokenEnums.openFile, "Expected OPENFILE.");

        ast.Expression filename = this.parseFileName();

        this.expect(TokenEnums.tokenFor, "Expected FOR after filename.");

        ast.FileMode mode = switch (this.currentType()) {
            case TokenEnums.read -> ast.FileMode.READ;
            case TokenEnums.write -> ast.FileMode.WRITE;

            default -> throw new IllegalArgumentException("Expected READ or WRITE after FOR.");
        };

        this.eat();

        return new OpenFileStatement(filename, mode);
    }

    private ast.ReadFileStatement parseReadFileStatement() {
        this.expect(TokenEnums.readFile, "Expected READFILE");

        ast.Expression filename = this.parseFileName();

        this.expect(TokenEnums.comma, "Expected ',' after file name.");

        ast.Assignable target = this.parseTarget();

        return new ReadFileStatement(filename, target);
    }

    private ast.WriteFileStatement parseWriteFileStatement() {
        this.expect(TokenEnums.writeFile, "Expected WRITEFILE.");

        ast.Expression filename = this.parseFileName();

        this.expect(TokenEnums.comma, "Expected ',' after file name.");

        ast.Expression value = this.parseExpression();

        return new WriteFileStatement(filename, value);
    }

    private ast.CloseFileStatement parseCloseFileStatement() {
        this.expect(TokenEnums.closeFile, "Expected CLOSEFILE.");

        ast.Expression filename = this.parseFileName();

        return new CloseFileStatement(filename);
    }

    private ast.Expression parseFileName() {
        if (this.currentType() == TokenEnums.identifier) {
            String name = ((Token) this.eat().getFirst()).getValue();
            return new Identifier(name);
        }

        if (this.currentType() == TokenEnums.doubleQuote) {
            return this.parsePrimaryExpression();
        }

        throw new IllegalArgumentException("Expected a string file name or file name variable.");
    }

    private ArrayList<ast.Statement> parseBlock(Set<TokenEnums> endingToken) {
        ArrayList<ast.Statement> body = new ArrayList<>();

        while (true) {

            // skip blanks
            while (this.currentType() == TokenEnums.eol) {
                this.eat();
            }

            if (endingToken.contains(this.currentType())) {
                return body;
            }

            if (this.currentType() == TokenEnums.eof) {
                throw new IllegalArgumentException("End of file reached when not intended. Expected: " + endingToken);
            }

            body.add(this.parseStatement());

            if (this.currentType() != TokenEnums.eol && this.currentType() != TokenEnums.eof) {
                throw new IllegalArgumentException("Expected end of line after statement.");
            }

        }
    }

    private ast.Input parseInput() {
        this.expect(TokenEnums.input, "Expected INPUT");

        return new Input(this.parseTarget());
    }

    private ast.Output parseOutput() {
        this.expect(TokenEnums.output, "Expected OUTPUT.");

        ArrayList<ast.Expression> values = new ArrayList<>();
        values.add(this.parseExpression());

        while (this.currentType() == TokenEnums.comma) {
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

        ast.TypeSpecification type = this.parseType();

        return new VariableDeclaration(name, type);
    }

    private ast.FunctionDeclaration parseFunctionDeclaration() {
        this.expect(TokenEnums.function, "Expected FUNCTION.");

        ArrayList<Object> tokenObject = this.expect(TokenEnums.identifier, "Expected function name.");

        String name = ((Token) tokenObject.getFirst()).getValue();

        ArrayList<ast.Parameter> parameters = new ArrayList<>();

        if (this.currentType() == TokenEnums.leftParen) {
            parameters = this.parseParameters();
        }

        this.expect(TokenEnums.returns, "Expected RETURNS after parameters.");

        ast.TypeSpecification returnType = this.parseType();

        this.expect(TokenEnums.eol, "Expected a new line after function statement.");

        ArrayList<ast.Statement> body = this.parseBlock(Set.of(TokenEnums.endFunction));

        this.expect(TokenEnums.endFunction, "Expected ENDFUNCTION.");

        return new FunctionDeclaration(name, parameters, returnType, body);
    }

    private ast.ProcedureDeclaration parseProcedureDeclaration() {
        this.expect(TokenEnums.procedure, "Expected PROCEDURE.");

        ArrayList<Object> tokenObject = this.expect(TokenEnums.identifier, "Expected procedure name.");

        String name = ((Token) tokenObject.getFirst()).getValue();

        ArrayList<ast.Parameter> parameters = new ArrayList<>();

        if (this.currentType() == TokenEnums.leftParen) {
            parameters = this.parseParameters();
        }

        this.expect(TokenEnums.eol, "Expected a new line after the declaration statement.");

        ArrayList<ast.Statement> body = this.parseBlock(Set.of(TokenEnums.endProcedure));

        this.expect(TokenEnums.endProcedure, "Expected ENDPROCEDURE.");

        return new ProcedureDeclaration(name, parameters, body);
    }

    private ast.ConstantDeclaration parseConstantDeclaration() {
        this.expect(TokenEnums.constant, "Expected CONSTANT.");

        ArrayList<Object> tokenObject = this.expect(TokenEnums.identifier, "Expected the constant's name.");

        String name = ((Token) tokenObject.getFirst()).getValue();

        this.expect(TokenEnums.assignment, "Expected '<-' after constant variable.");

        ast.Expression value = this.parseConstantLiteral();

        if (this.currentType() != TokenEnums.eol && this.currentType() != TokenEnums.eof)  {
            throw new IllegalArgumentException("A constant value must be a single literal.");
        }

        return new ConstantDeclaration(name, value);
    }

    private ast.Expression parseConstantLiteral() {

        if (this.currentType() == TokenEnums.subtraction) {
            this.eat();

            if (this.currentType() != TokenEnums.literalInteger && this.currentType() != TokenEnums.literalReal) {
                throw new IllegalArgumentException("Expected a numeric literal after '-'.");
            }

            String value = ((Token) this.eat().getFirst()).getValue();

            return new NumericLiteral("-" + value);

        }

        return switch (this.currentType()) {
            case TokenEnums.literalInteger, TokenEnums.literalReal, TokenEnums.doubleQuote, TokenEnums.singleQuote, TokenEnums.literalBoolean -> this.parsePrimaryExpression();

            default -> throw new IllegalArgumentException("Expected a literal constant value, got: " + this.currentType());
        };
    }

    private ast.Assignment parseAssignment() {
        ast.Assignable target = this.parseTarget();

        this.expect(TokenEnums.assignment,"Expected '<-' after assignment target.");

        ast.Expression value = this.parseExpression();

        return new Assignment(target, value);
    }

    private ArrayList<ast.Expression> parseArguments() {
        this.expect(TokenEnums.leftParen, "Expected '(' before arguments.");

        ArrayList<ast.Expression> arguments = new ArrayList<>();

        if (this.currentType() != TokenEnums.rightParen) {
            arguments.add(this.parseExpression());

            while (this.currentType() == TokenEnums.comma) {
                this.eat();
                arguments.add(this.parseExpression());
            }
        }

        this.expect(TokenEnums.rightParen, "Expected ')' after arguments.");

        return arguments;
    }

    private ast.TypeSpecification parseType() {
        if (this.currentType() == TokenEnums.typeArray) {
            this.eat();

            this.expect(TokenEnums.leftBracket, "Expected '[' after ARRAY.");

            ArrayList<ast.ArrayBounds> bounds = new ArrayList<>();

            while (true) {
                ast.Expression lower = this.parseExpression();

                this.expect(TokenEnums.colon, "Expected ':' between the bounds.");

                ast.Expression upper = this.parseExpression();

                bounds.add(new ast.ArrayBounds(lower, upper));

                if (this.currentType() != TokenEnums.comma) {
                    break;
                }

                this.eat();
            }

            this.expect(TokenEnums.rightBracket, "Expected ']' after the bounds declaration.");

            this.expect(TokenEnums.of, "Expected OF after array.");

            ast.TypeSpecification elementType = this.parseType();

            return new ast.ArrayType(bounds, elementType);
        }

        TokenEnums type = this.currentType();

        switch (type) {
            case TokenEnums.typeInteger, TokenEnums.typeReal, TokenEnums.typeChar, TokenEnums.typeString, TokenEnums.typeBoolean: {
                this.eat();
                return new ast.ScalarType(type);
            }

            default: {
                throw new IllegalArgumentException("Expected a data type, got: " + type);
            }
        }
    }

    private ast.Assignable parseTarget() {
        if (this.currentType() != TokenEnums.identifier) {
            throw new IllegalArgumentException("Expected a variable or array element.");
        }

        ast.Expression expression = this.parseArrayAccessExpression();

        if (expression instanceof ast.Assignable target) {
            return target;
        }

        throw new IllegalArgumentException("Expression can not be used as an assignment target.");
    }

    private ArrayList<ast.Parameter> parseParameters() {
        this.expect(TokenEnums.leftParen, "Expected '(' before parameters.");

        ArrayList<ast.Parameter> parameters = new ArrayList<>();

        if (this.currentType() != TokenEnums.rightParen) {
            while (true) {
                ArrayList<Object> tokenObject = this.expect(TokenEnums.identifier, "Expected parameter name.");

                String name = ((Token) tokenObject.getFirst()).getValue();

                this.expect(TokenEnums.colon, "Expected ':' after parameter name.");

                ast.TypeSpecification type = this.parseType();

                parameters.add(new ast.Parameter(name, type));

                if (this.currentType() != TokenEnums.comma) {
                    break;
                }

                this.eat();
            }
        }

        this.expect(TokenEnums.rightParen, "Expected ')' after parameters.");

        return parameters;
    }







    // expressions start here
    private ast.Expression parseExpression() {
        return this.parseOrExpression();
    }

    private ast.Expression parseOrExpression() {
        ast.Expression left = this.parseAndExpression();

        while (this.currentType() == TokenEnums.or) {
            String operator = ((Token) this.eat().getFirst()).getValue();
            ast.Expression right = this.parseAndExpression();

            left = new BinaryExpression(left, right, operator);
        }

        return left;
    }

    private ast.Expression parseAndExpression() {
        ast.Expression left = this.parseNotExpression();

        while (this.currentType() == TokenEnums.and) {
            String operator = ((Token) this.eat().getFirst()).getValue();
            ast.Expression right = this.parseNotExpression();

            left = new BinaryExpression(left, right, operator);
        }

        return left;
    }

    private ast.Expression parseNotExpression() {
        if (this.currentType() == TokenEnums.not) {
            String operator = ((Token) this.eat().getFirst()).getValue();
            ast.Expression operand = this.parseNotExpression();

            return new UnaryExpression(operator, operand);
        }

        return this.parseComparisonExpression();
    }

    private ast.Expression parseComparisonExpression() {
        ast.Expression left = this.parseConcatenationExpression();

        if (this.isComparisonOperator()) {
            String operator = ((Token) this.eat().getFirst()).getValue();
            ast.Expression right = this.parseConcatenationExpression();

            left = new BinaryExpression(left, right, operator);

            if (this.isComparisonOperator()) {
                throw new IllegalArgumentException("Comparison operators can not be used in a row.");
            }
        }

        return left;
    }

    private ast.Expression parseConcatenationExpression() {
        ast.Expression left = this.parseAdditiveExpression();

        while (this.currentType() == TokenEnums.concatenate) {
            String operator = ((Token) this.eat().getFirst()).getValue();

            ast.Expression right = this.parseAdditiveExpression();

            left = new BinaryExpression(left, right, operator);
        }

        return left;
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
        ast.Expression left = this.parseUnaryExpression();

        String tkValue = this.currentValue();

        while ( tkValue.equals("*") || tkValue.equals("/") || tkValue.equals("MOD") ||tkValue.equals("DIV") ) {
            final String operator = ((Token)this.eat().getFirst()).getValue();
            final ast.Expression right = this.parseUnaryExpression();

            left = new BinaryExpression(left, right, operator);

            tkValue = this.currentValue();

        }

        return left;
    }

    private ast.Expression parseUnaryExpression() {
        if (this.currentType() == TokenEnums.subtraction) {
            String operator = ((Token) this.eat().getFirst()).getValue();

            ast.Expression operand = this.parseUnaryExpression();

            return new UnaryExpression(operator, operand);
        }

        return this.parsePowerExpression();
    }

    private ast.Expression parsePowerExpression() {
        ast.Expression left = this.parseArrayAccessExpression();

        if (this.currentType() == TokenEnums.exponent) {
            String operator = ((Token) this.eat().getFirst()).getValue();

            ast.Expression right = this.parseUnaryExpression();

            return new BinaryExpression(left, right, operator);
        }

        return left;
    }

    private ast.Expression parseArrayAccessExpression() {
        ast.Expression expression = this.parsePrimaryExpression();

        if (this.currentType() == TokenEnums.leftBracket) {
            if (!(expression instanceof ast.Identifier base)) {
                throw new IllegalArgumentException("Expected an array identifier before '['.");
            }

            this.eat();

            ArrayList<ast.Expression> indices = new ArrayList<>();

            indices.add(this.parseExpression());

            while (this.currentType() == TokenEnums.comma)  {
                this.eat();
                indices.add(this.parseExpression());
            }

            this.expect(TokenEnums.rightBracket, "Expected ']' after array indices.");

            expression = new IndexExpression(base, indices);
        }

        return expression;
    }

    private ast.Expression parsePrimaryExpression() {

        ArrayList<Object> tk = this.at();
        TokenEnums tkType = ( (TokenEnums) tk.get(1));
        String tkValue = ((Token)this.eat().getFirst()).getValue(); // gets the eaten value

        switch(tkType) {
            case TokenEnums.identifier: {
                String name = tkValue;

                if (this.currentType() == TokenEnums.leftParen) {
                    return new CallExpression(name, this.parseArguments());
                }

                return new Identifier(name);
            }
            case TokenEnums.length, TokenEnums.lCase, TokenEnums.uCase, TokenEnums.subString, TokenEnums.round, TokenEnums.random, TokenEnums.div, TokenEnums.mod: {
                return new CallExpression(tkValue, this.parseArguments());
            }
            case TokenEnums.literalInteger, TokenEnums.literalReal: {
                return new NumericLiteral(tkValue);
            }
            case TokenEnums.doubleQuote: {
                ArrayList<Object> tokenObject = this.expect(TokenEnums.literalString, "Expected string literal after opening quote.");

                String value = ((Token) tokenObject.getFirst()).getValue();

                this.expect(TokenEnums.doubleQuote, "Expected closing quotations.");

                return new TextLiteral(value, ast.TextType.string);
            }
            case TokenEnums.singleQuote: {
                ArrayList<Object> tokenObject = this.expect(TokenEnums.literalChar, "Expected character literal after opening quote.");

                String value = ((Token) tokenObject.getFirst()).getValue();

                if (value.length() != 1) { throw new IllegalArgumentException("Character literal can only contain one character."); }

                this.expect(TokenEnums.singleQuote, "Expected closing quote after character literal.");

                return new TextLiteral(value, ast.TextType.character);
            }
            case TokenEnums.literalBoolean: {
                return switch (tkValue) {
                    case "TRUE" -> new BooleanLiteral(true);
                    case "FALSE" -> new BooleanLiteral(false);
                    default -> throw new IllegalArgumentException("Invalid boolean literal: " + tkValue);
                };
            }
            case TokenEnums.leftParen: {
                final ast.Expression value = this.parseExpression();
                this.expect(TokenEnums.rightParen, "Unexpected token found inside parenthesised expression. Expected closing parenthesis.");
                return value;
            }
            default: {
                throw new IllegalArgumentException("Expected an expression, got: " + tkType + ",value '" + tkValue + "'");
            }
        }

    }


}
