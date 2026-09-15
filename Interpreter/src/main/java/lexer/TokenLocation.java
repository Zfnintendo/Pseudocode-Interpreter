package lexer;

public class TokenLocation {

    int line;
    int column;

    public TokenLocation(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public int getLine() {
        return this.line;
    }

    public int getColumn() {
        return this.column;
    }

}
