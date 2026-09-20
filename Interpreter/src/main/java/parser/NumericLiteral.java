package parser;

public class NumericLiteral implements ast.NumericLiteral {

    public double value;

    public NumericLiteral(String value) {
        this.value = Double.parseDouble(value);
    }
}


