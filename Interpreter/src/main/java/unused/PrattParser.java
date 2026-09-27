// good references
// https://www.youtube.com/watch?v=0c8b7YfsBKs
// https://journal.stuffwithstuff.com/2011/03/19/pratt-parsers-expression-parsing-made-easy/
// https://matklad.github.io/2020/04/13/simple-but-powerful-pratt-parsing.html

package unused;

import java.util.ArrayList;

public class PrattParser {

    // consumes the next token in the reversed list
    public static ArrayList<Object> next(ArrayList<ArrayList<Object>> tokenList) {
        ArrayList<Object> token = tokenList.removeLast();
        return token;
    }

    // peeks the next token in the reversed list
    public static ArrayList<Object> peek(ArrayList<ArrayList<Object>> tokenList) {
        ArrayList<Object> lastToken = tokenList.get(tokenList.size() - 1);
        ArrayList<Object> token = new ArrayList<Object>(lastToken);
        return token;
    }

    public static InfixPowers infixBindingPower(String op) {
        return switch (op) {
            case "+", "-" -> new InfixPowers(1.0, 1.1);
            case "*", "/" -> new InfixPowers(2.0, 2.1);
            case "^" -> new InfixPowers(3.1, 3.0);
            default -> throw new IllegalArgumentException("Unknown operator" + op);
        };
    }







}
