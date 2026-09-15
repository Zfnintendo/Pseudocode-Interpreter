package lexer;

import java.util.ArrayList;

public class AddToken {

    ArrayList<ArrayList<Object>> tokenArray = new ArrayList<ArrayList<Object>>();

    public AddToken() {}

    public void add(Token token, TokenEnums tokenEnums, String character) {
        ArrayList<Object> tokenObject = new ArrayList<Object>();

        tokenObject.add(token);
        tokenObject.add(tokenEnums);
        tokenObject.add(character);

        this.tokenArray.add(tokenObject);
    }

    public ArrayList<ArrayList<Object>> getTokens() {
        return this.tokenArray;
    }

}
