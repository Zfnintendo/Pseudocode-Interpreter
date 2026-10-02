package evaluator;

import ast.*;

import java.util.List;
import java.util.Optional;

public class AstPrinter {

    private AstPrinter() {}

    public static void printTree(Program program) {
        printNode("Program", program, 0);
    }

    private static void printNode(String label, Object value, int depth) {

        System.out.println(" ".repeat(depth) + label + ": " + describe(value));

        int nextDepth = depth + 1;

        switch (value) {
            case Program program -> children(nextDepth,"body", program.body());

            // ok i think these lines are getting really long if i use their full names so we sub for n
            case VariableDeclaration n -> children(nextDepth, "name", n.name(), "declaredType", n.declaredType());
            case ConstantDeclaration n -> children(nextDepth, "name", n.name(), "value", n.value());

            case Assignment n -> children(nextDepth, "target", n.target(), "value", n.value());

            case BinaryExpression n -> children(nextDepth, "operator", n.operator(), "left", n.LHS(), "right", n.RHS());
            case UnaryExpression n -> children(nextDepth, "operator", n.operator(), "operand", n.operand());
            case IndexExpression n -> children(nextDepth, "base", n.base(), "indices", n.indices());


            default -> throw new IllegalArgumentException("Unsupported AST object: " + value.getClass().getName());
        }

    }

    private static void children(int depth, Object... fields) {
        if (fields.length % 2 != 0) {
            throw new IllegalArgumentException("Expected label-value pairs.");
        }

        for (int i = 0; i < fields.length; i += 2) {
            String label = (String) fields[i];
            Object value = fields[i + 1];

            printNode(label, value, depth);
        }
    }

    private static String describe(Object value) {
        return switch (value) {
            case Program program -> "Program";

            case Identifier identifier -> "Identifier(" + identifier.symbol() + ")";

            case NumericLiteral numericLiteral -> "NumericLiteral(" + numericLiteral.value() + ")";
            case TextLiteral textLiteral -> "TextLiteral(" + textLiteral.type() + ", " + quote(textLiteral.value()) + ")";
            case BooleanLiteral booleanLiteral -> "BooleanLiteral(" + booleanLiteral.value() + ")";

            case ScalarType scalarType -> "ScalarType(" + scalarType.tokenType() + ")";
            case ArrayType arrayType -> "ArrayType";
            case ArrayBounds arrayBounds -> "ArrayBounds";
            case Parameter parameter -> "Parameter";
            case Cases cases -> "CaseBranch";

            case Statement statement -> statement.kind().name();

            case List<?> items -> "List[" +items.size() + ']';

            case Optional<?> optional -> optional.isPresent()? "present" : "absent";

            case String text -> quote(text);
            case FileMode mode -> mode.name();

            default -> throw new IllegalArgumentException("Unsupported AST object: " + value.getClass().getName());
        };
    }

    private static String quote(String text) {
        return "\"" + text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
                + "\"";
    }
}
