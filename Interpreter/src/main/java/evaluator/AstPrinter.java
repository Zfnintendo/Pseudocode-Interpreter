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

            case Input n -> children(nextDepth, "target", n.target());
            case Output n -> children(nextDepth, "values", n.values());

            case IfStatement n -> children(nextDepth, "condition", n.condition(), "thenBody", n.thenBody(), "elseBody", n.elseBody());
            case WhileStatement n -> children(nextDepth, "condition", n.condition(), "body", n.body());
            case RepeatStatement n -> children(nextDepth, "body", n.body(), "condition", n.condition());
            case ForStatement n -> children(nextDepth, "variable", n.variable(), "start", n.start(), "end", n.end(), "step", n.step(), "body", n.body());
            case ReturnStatement n -> children(nextDepth, "value", n.value());

            case CaseStatement n -> children(nextDepth, "selector", n.selector(), "cases", n.cases(), "otherwise", n.otherwiseCase());
            case Cases n -> children(nextDepth, "value", n.value(), "statement", n.statement());

            case CallStatement n -> children(nextDepth, "call", n.call());
            case CallExpression n -> children(nextDepth, "name", n.name(), "arguments", n.arguments());

            case FunctionDeclaration n -> children(nextDepth, "name", n.name(), "parameters", n.parameters(), "returnType", n.returnType(), "body", n.body());
            case ProcedureDeclaration n -> children(nextDepth, "name", n.name(), "parameters", n.parameters(), "body", n.body());

            case Parameter n -> children(nextDepth, "name", n.name());

            case ArrayType n -> children(nextDepth, "bounds", n.bounds(), "elementType", n.elementType());
            case ArrayBounds n -> children(nextDepth, "lower", n.lower(), "upper", n.upper());

            case OpenFileStatement n -> children(nextDepth, "filename", n.filename(), "mode", n.mode());
            case ReadFileStatement n -> children(nextDepth, "filename", n.filename(), "target", n.target());
            case WriteFileStatement n -> children(nextDepth, "filename", n.filename(), "value", n.value());
            case CloseFileStatement n -> children(nextDepth, "filename", n.filename());

            case Identifier ignore -> {}
            case NumericLiteral ignore -> {}
            case TextLiteral ignore -> {}
            case BooleanLiteral ignore -> {}
            case ScalarType ignore -> {}
            case String ignore -> {}
            case FileMode ignore -> {}

            case List<?> items -> {
                for (int i = 0; i < items.size(); i++) {
                    printNode("[" + i + "]", items.get(i), nextDepth);
                }
            }
            case Optional<?> optional -> {
                if (optional.isPresent()) {
                    printNode("value", optional.get(), nextDepth);
                }
            }

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

            case Optional<?> optional -> optional.isPresent() ? "present" : "absent";

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
