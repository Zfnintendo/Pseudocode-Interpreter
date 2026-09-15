package lexer;

public class Token {

    // token data
    String type;
    String value;
    TokenLocation location;


    public Token(String type, String value, TokenLocation location) {
        this.type = type;
        this.value = value;
        this.location = location;
    }

    public String getType() {
        return this.type;
    }

    public String getValue() {
        return this.value;
    }

    public TokenLocation getLocation() {
        return this.location;
    }


}
