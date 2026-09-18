package lexer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class MainLexer {

    private final File file;
    private ArrayList<ArrayList<Object>> lexedLine;

    public MainLexer(File file) {
        this.file = file;
    }

    public ArrayList<ArrayList<Object>> lexFile() throws FileNotFoundException {

        Scanner scanner = new Scanner(this.file);

        int i = 1;
        while (scanner.hasNextLine()) {
            LineTokenizer lineTokenizer = new LineTokenizer(scanner.nextLine(), i);
            this.lexedLine.addAll( lineTokenizer.tokenize() ); // adding it to the lexedLine the line that we lexed!
            i++;
        }
        // after this we should have gotten a full token array full of tokens

        // add end of file token
        AddToken addToken = new AddToken();
        TokenLocation tokenLocation = new TokenLocation(i,0);
        Token token = new Token(TokenType.control, "", tokenLocation);

        addToken.addToLine(token, TokenEnums.eof);
        ArrayList<ArrayList<Object>> endOfFile = addToken.getTokens();

        this.lexedLine.addAll(endOfFile);
        // finish that

        return this.lexedLine;

    }

}
