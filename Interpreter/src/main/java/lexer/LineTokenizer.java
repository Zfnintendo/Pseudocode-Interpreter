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

            }

        }
    }

}
