package lexer;

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

            char c = this.line.charAt(i);
            switch(c) {
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
                default: {

                }

            }

        }
    }

}
