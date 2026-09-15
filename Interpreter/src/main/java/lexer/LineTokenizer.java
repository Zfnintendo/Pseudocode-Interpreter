package lexer;

public class LineTokenizer {

    String line;
    int lineNumber;

    public LineTokenizer(String line, int lineNumber) {
        this.line = line;
        this.lineNumber = lineNumber;
    }

    public void tokenize() {
        AddToken addToken = new AddToken();

        for (int i = 0; i < this.line.length(); i++) {
            TokenLocation tokenLocation = new TokenLocation(this.lineNumber, i);

            char c = this.line.charAt(i);
            switch(c) {
                case ' ': {
                    break; // do nothing
                }
                case '+': {
                    Token token = new Token(TokenType.operator, "+", tokenLocation);
                    addToken.add(token, TokenEnums.addition);
                    break;
                }
                case '-': {
                    Token token = new Token(TokenType.operator, "-", tokenLocation);
                    addToken.add(token, TokenEnums.addition);
                    break;
                }
                case '*': {
                    Token token = new Token(TokenType.operator, "*", tokenLocation);
                    addToken.add(token, TokenEnums.multiplication);
                    break;
                }
                case '/': {
                    Token token = new Token(TokenType.operator, "/", tokenLocation);
                    addToken.add(token, TokenEnums.division);
                    break;
                }
                case '^': {
                    Token token = new Token(TokenType.operator, "^", tokenLocation);
                    addToken.add(token, TokenEnums.exponent);
                    break;
                }
                case '=': {
                    Token token = new Token(TokenType.operator, "=", tokenLocation);
                    addToken.add(token, TokenEnums.equals);
                    break;
                }
                case '<': {
                    Token token = new Token(TokenType.operator, "<", tokenLocation);
                    addToken.add(token, TokenEnums.lessThan);
                    break;
                }
                case '>': {
                    Token token = new Token(TokenType.operator, ">", tokenLocation);
                    addToken.add(token, TokenEnums.greaterThan);
                    break;
                }
                case '(': {
                    Token token = new Token(TokenType.punctuation, "(", tokenLocation);
                    addToken.add(token, TokenEnums.leftParen);
                    break;
                }
                case ')': {
                    Token token = new Token(TokenType.punctuation, ")", tokenLocation);
                    addToken.add(token, TokenEnums.rightParen);
                    break;
                }
                case '[': {
                    Token token = new Token(TokenType.punctuation, "[", tokenLocation);
                    addToken.add(token, TokenEnums.leftBracket);
                    break;
                }
                case ']': {
                    Token token = new Token(TokenType.punctuation, "]", tokenLocation);
                    addToken.add(token, TokenEnums.rightBracket);
                    break;
                }
                case '"': {
                    Token token = new Token(TokenType.punctuation, "\"", tokenLocation);
                    addToken.add(token, TokenEnums.doubleQuote);
                    break;
                }
                case '\'': {
                    Token token = new Token(TokenType.punctuation, "'", tokenLocation);
                    addToken.add(token, TokenEnums.singleQuote);
                    break;
                }
                case ',': {
                    Token token = new Token(TokenType.punctuation, ",", tokenLocation);
                    addToken.add(token, TokenEnums.comma);
                    break;
                }
                case ':': {
                    Token token = new Token(TokenType.punctuation, ":", tokenLocation);
                    addToken.add(token, TokenEnums.colon);
                    break;
                }
                default: {

                }

            }

        }
    }

}
