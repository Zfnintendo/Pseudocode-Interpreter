package lexer;

public class TokenLocation {

    Integer line;
    Integer column;

    public TokenLocation(Integer line, Integer column) {
        this.line = line;
        this.column = column;
    }

    public Integer getLine() {
        return this.line;
    }

    public Integer getColumn() {
        return this.column;
    }

}
