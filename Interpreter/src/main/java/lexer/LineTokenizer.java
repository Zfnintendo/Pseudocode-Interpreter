package lexer;

// use this class by creating a new instance of it then calling tokenize to get the table of tokens

public class LineTokenizer {

    private String line;
    private int lineNumber;

    public LineTokenizer(String line, int lineNumber) {
        this.line = line;
        this.lineNumber = lineNumber;
    }

    public void tokenize() {
        AddToken addToken = new AddToken();

        for (int i = 0; i < this.line.length(); i++) {
            TokenLocation tokenLocation = new TokenLocation(this.lineNumber, i);

            char c0 = this.line.charAt(i);
            switch(c0) { // check for symbol tokens or 1 character tokens | ONE CHARACTER STARTS HERE!
                case ' ': {
                    break; // do nothing
                }
                case '+': {
                    Token token = new Token(TokenType.operator, "+", tokenLocation);
                    addToken.addToLine(token, TokenEnums.addition);
                    break;
                }
                case '-': {
                    Token token = new Token(TokenType.operator, "-", tokenLocation);
                    addToken.addToLine(token, TokenEnums.addition);
                    break;
                }
                case '*': {
                    Token token = new Token(TokenType.operator, "*", tokenLocation);
                    addToken.addToLine(token, TokenEnums.multiplication);
                    break;
                }
                case '/': {
                    Token token = new Token(TokenType.operator, "/", tokenLocation);
                    addToken.addToLine(token, TokenEnums.division);
                    break;
                }
                case '^': {
                    Token token = new Token(TokenType.operator, "^", tokenLocation);
                    addToken.addToLine(token, TokenEnums.exponent);
                    break;
                }
                case '=': {
                    Token token = new Token(TokenType.operator, "=", tokenLocation);
                    addToken.addToLine(token, TokenEnums.equals);
                    break;
                }
                case '<': {
                    Token token = new Token(TokenType.operator, "<", tokenLocation);
                    addToken.addToLine(token, TokenEnums.lessThan);
                    break;
                }
                case '>': {
                    Token token = new Token(TokenType.operator, ">", tokenLocation);
                    addToken.addToLine(token, TokenEnums.greaterThan);
                    break;
                }
                case '(': {
                    Token token = new Token(TokenType.punctuation, "(", tokenLocation);
                    addToken.addToLine(token, TokenEnums.leftParen);
                    break;
                }
                case ')': {
                    Token token = new Token(TokenType.punctuation, ")", tokenLocation);
                    addToken.addToLine(token, TokenEnums.rightParen);
                    break;
                }
                case '[': {
                    Token token = new Token(TokenType.punctuation, "[", tokenLocation);
                    addToken.addToLine(token, TokenEnums.leftBracket);
                    break;
                }
                case ']': {
                    Token token = new Token(TokenType.punctuation, "]", tokenLocation);
                    addToken.addToLine(token, TokenEnums.rightBracket);
                    break;
                }
                case '"': {
                    Token token = new Token(TokenType.punctuation, "\"", tokenLocation);
                    addToken.addToLine(token, TokenEnums.doubleQuote);
                    break;
                }
                case '\'': {
                    Token token = new Token(TokenType.punctuation, "'", tokenLocation);
                    addToken.addToLine(token, TokenEnums.singleQuote);
                    break;
                }
                case ',': {
                    Token token = new Token(TokenType.punctuation, ",", tokenLocation);
                    addToken.addToLine(token, TokenEnums.comma);
                    break;
                }
                case ':': {
                    Token token = new Token(TokenType.punctuation, ":", tokenLocation);
                    addToken.addToLine(token, TokenEnums.colon);
                    break;
                }
                default: { // TWO CHARACTERS START HERE
                    if (i + 1 >= this.line.length()) { continue; } // check if i+1 exists in the string
                }

                char c1 = this.line.charAt(i+1);
                String stringA = String.valueOf(new char[]{c0, c1});

                switch(stringA) { // checks for two character tokens
                    case "<-": {
                        Token token = new Token(TokenType.operator, "<-", tokenLocation);
                        addToken.addToLine(token, TokenEnums.assignment);
                        i++; // we skip the next iteration as thats basically the same token
                        break;
                    }
                    case "OR": {
                        Token token = new Token(TokenType.operator, "OR", tokenLocation);
                        addToken.addToLine(token, TokenEnums.or);
                        i++;
                        break;
                    }
                    case "<=": {
                        Token token = new Token(TokenType.operator, "<=", tokenLocation);
                        addToken.addToLine(token, TokenEnums.lessEqualTo);
                        i++;
                        break;
                    }
                    case ">=": {
                        Token token = new Token(TokenType.operator, ">=", tokenLocation);
                        addToken.addToLine(token, TokenEnums.greaterEqualTo);
                        i++;
                        break;
                    }
                    case "<>": {
                        Token token = new Token(TokenType.operator, "<>", tokenLocation);
                        addToken.addToLine(token, TokenEnums.notEqualTo);
                        i++;
                        break;
                    }
                    case "OF": {
                        Token token = new Token(TokenType.keyword, "OF", tokenLocation);
                        addToken.addToLine(token, TokenEnums.of);
                        i++;
                        break;
                    }
                    case "IF": {
                        Token token = new Token(TokenType.keyword, "IF", tokenLocation);
                        addToken.addToLine(token, TokenEnums.tokenIf);
                        i++;
                        break;
                    }
                    default: { // THREE CHARACTERS START HERE!
                        if (i+2 >= this.line.length()) { continue; }
                    }

                } // end of second switch

            } // end of first switch

        } // end of for loop

        TokenLocation tokenLocation = new TokenLocation(this.lineNumber, this.line.length());
        Token token = new Token(TokenType.control, "", tokenLocation);
        addToken.addToLine(token, TokenEnums.eol);

    }

}
