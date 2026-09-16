package lexer;

public class Token {

    // token data
    private TokenType type;
    private String value;
    private TokenLocation location;


    public Token(TokenType type, String value, TokenLocation location) {
        this.type = type;
        this.value = value;
        this.location = location;
    }

    public TokenType getType() {
        return this.type;
    }

    public String getValue() {
        return this.value;
    }

    public TokenLocation getLocation() {
        return this.location;
    }


}
