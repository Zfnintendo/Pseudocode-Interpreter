package lexer;

import java.util.ArrayList;

public class AddToken {

    private ArrayList<ArrayList<Object>> tokenArray = new ArrayList<ArrayList<Object>>();

    public AddToken() {}

    public void addToLine(Token token, TokenEnums tokenEnums) {
        ArrayList<Object> tokenObject = new ArrayList<Object>();

        tokenObject.add(token);
        tokenObject.add(tokenEnums);

        this.tokenArray.add(tokenObject);
    }

    public ArrayList<ArrayList<Object>> getTokens() {
        return this.tokenArray;
    }

}


