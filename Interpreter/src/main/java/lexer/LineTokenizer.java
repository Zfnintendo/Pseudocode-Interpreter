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
            } // end of first switch


            if (i + 1 >= this.line.length()) { continue; } // check if i+1 exists in the string


            // start of second switch
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
            } // end of second switch


            if (i+2 >= this.line.length()) { continue; }


            // Start of third switch
            char c2 = this.line.charAt(i+2);
            StringBuilder sb0 = new StringBuilder(stringA);
            sb0.append(c2);
            String stringB = sb0.toString();
            switch(stringB) {
                case "DIV": {
                    Token token = new Token(TokenType.keyword, "DIV", tokenLocation);
                    addToken.addToLine(token, TokenEnums.div);
                    i += 2;
                    break;
                }
                case "MOD": {
                    Token token = new Token(TokenType.keyword, "MOD", tokenLocation);
                    addToken.addToLine(token, TokenEnums.mod);
                    i += 2;
                    break;
                }
                case "AND": {
                    Token token = new Token(TokenType.operator, "AND", tokenLocation);
                    addToken.addToLine(token, TokenEnums.and);
                    i += 2;
                    break;
                }
                case "NOT": {
                    Token token = new Token(TokenType.operator, "NOT", tokenLocation);
                    addToken.addToLine(token, TokenEnums.not);
                    i += 2;
                    break;
                }
                case "FOR": {
                    Token token = new Token(TokenType.keyword, "FOR", tokenLocation);
                    addToken.addToLine(token, TokenEnums.tokenFor);
                    i += 2;
                    break;
                }
            } // end of third switch


            if (i+3 >= this.line.length()) { continue; }


            // Start of fourth switch
            char c3 = this.line.charAt(i+3);
            StringBuilder sb1 = new StringBuilder(stringB);
            sb1.append(c3);
            String stringC = sb1.toString();
            switch(stringC) {
                case "REAL": {
                    Token token = new Token(TokenType.keyword, "REAL", tokenLocation);
                    addToken.addToLine(token, TokenEnums.typeReal);
                    i += 3;
                    break;
                }
            } // end of fourth switch



        } // end of for loop

        TokenLocation tokenLocation = new TokenLocation(this.lineNumber, this.line.length());
        Token token = new Token(TokenType.control, "", tokenLocation);
        addToken.addToLine(token, TokenEnums.eol);

    }

}
