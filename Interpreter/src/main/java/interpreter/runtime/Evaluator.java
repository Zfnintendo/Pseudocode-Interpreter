package interpreter.runtime;

import interpreter.values.*;
import ast.*;

public class Evaluator {

    public Evaluator() {}

    public static RuntimeValue evaluate(Statement astNode) {

        switch(astNode.kind()) {
            case NodeType.program: {
                return evaluateProgram( (Program) astNode );
            }
            case NodeType.numericLiteral: {

                ast.NumericLiteral literal = (ast.NumericLiteral) astNode;
                double n = Double.parseDouble(literal.value());
                return new NumberValue(n);

            }
            case NodeType.binaryExpression: {
                return evaluateBinaryExpression( (BinaryExpression) astNode );
            }
            default: {
                throw new UnsupportedOperationException("This AST node has not been set up for interpretation: " + astNode);
            }
        }

    }

    public static RuntimeValue evaluateProgram(Program program) {
        RuntimeValue lastEvaluated = new NullValue();

        for (Statement statement : program.body()) {
            lastEvaluated = evaluate(statement);
        }

        return lastEvaluated;
    }

    public static RuntimeValue evaluateBinaryExpression(BinaryExpression binop) {
        RuntimeValue lhs = evaluate(binop.LHS());
        RuntimeValue rhs = evaluate(binop.RHS());

        if (lhs.type() == ValueType.number && rhs.type() == ValueType.number) {
            NumberValue left = (NumberValue) lhs;
            NumberValue right = (NumberValue) rhs;

            return evaluateNumericBinaryExpression(left, right, binop.operator());
        }

        throw new IllegalArgumentException("Operator '" + binop.operator() + "' does not support " + lhs.type() + " and " + rhs.type());

    }

    public static NumberValue evaluateNumericBinaryExpression(NumberValue lhs, NumberValue rhs, String operator) {
        double result = switch(operator) {
            case "+" -> lhs.value() + rhs.value();
            case "-" -> lhs.value() - rhs.value();
            case "*" -> lhs.value() * rhs.value();
            case "/" -> {
                if (rhs.value() == 0.0) {
                    throw new ArithmeticException("Division by zero is not possible.");
                }

                yield lhs.value() / rhs.value();
            }
            case "^" -> Math.pow(lhs.value(), rhs.value());

            default -> throw new UnsupportedOperationException("Operator can not be implemeneted into evaluator." + operator);
        };

        return new NumberValue(result);
    }

}
