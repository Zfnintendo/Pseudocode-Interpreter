package lexer;

public class TokenLocation {

    private int line;
    private int column;

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

    public void changeLine(int line) {
        this.line = line;
    }

    public void changeColumn(int column) {
        this.column = column;
    }

}
