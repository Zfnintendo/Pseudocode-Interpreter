package interpreter.runtime;

import interpreter.values.*;
import ast.*;

public class Evaluator {

    public Evaluator() {}

    public static RuntimeValue evaluate(Statement astNode, Environment environment) {

        return switch(astNode.kind()) {
            case NodeType.program -> evaluateProgram( (Program) astNode, environment);

            case NodeType.numericLiteral -> {
                NumericLiteral literal = (NumericLiteral) astNode;
                double n = Double.parseDouble(literal.value());
                yield new NumberValue(n);
            }
            case NodeType.textLiteral -> {
                TextLiteral literal = (TextLiteral) astNode;

                yield switch (literal.type()) {
                    case TextType.string -> new StringValue(literal.value());
                    case TextType.character -> {
                        if (literal.value().length() != 1) {
                            throw new IllegalArgumentException("Character literal must contain one character.");
                        }
                        yield new CharValue(literal.value().charAt(0));
                    }
                };
            }
            case NodeType.booleanLiteral -> {
                BooleanLiteral literal = (BooleanLiteral) astNode;
                yield new BooleanValue(literal.value());
            }

            case NodeType.binaryExpression -> evaluateBinaryExpression( (BinaryExpression) astNode, environment);
            case NodeType.unaryExpression -> evaluateUnaryExpression( (UnaryExpression) astNode, environment);

            case NodeType.variableDeclaration -> {
                VariableDeclaration declaration = (VariableDeclaration) astNode;

                if (!(declaration.declaredType() instanceof ScalarType(lexer.TokenEnums tokenType))
                        || (tokenType != lexer.TokenEnums.typeInteger
                        && tokenType != lexer.TokenEnums.typeReal)) {
                    throw new UnsupportedOperationException("Only numeric scalar declarations are supported yet.");
                }

                environment.declareVariable(declaration.name(), declaration.declaredType());

                yield new NullValue();
            }

            case NodeType.identifier -> {
                Identifier identifier = (Identifier) astNode;
                yield environment.read(identifier.symbol());
            }

            case NodeType.assignment -> {
                Assignment assignment = (Assignment) astNode;

                if (!(assignment.target() instanceof Identifier target)) {
                    throw new UnsupportedOperationException("Array assignment is not implmented yet.");
                }

                RuntimeValue value = evaluate(assignment.value(), environment);

                environment.assign(target.symbol(), value);

                yield new NullValue();
            }

            case NodeType.output -> evaluateOutput( (Output) astNode, environment );

            default -> throw new UnsupportedOperationException("This AST node has not been set up for interpretation: " + astNode.kind());
        };

    }

    public static RuntimeValue evaluateProgram(Program program, Environment environment) {
        RuntimeValue lastEvaluated = new NullValue();

        for (Statement statement : program.body()) {
            lastEvaluated = evaluate(statement, environment);
        }

        return lastEvaluated;
    }

    public static RuntimeValue evaluateBinaryExpression(BinaryExpression binop, Environment environment) {
        RuntimeValue lhs = evaluate(binop.LHS(), environment);

        if (binop.operator().equals("AND") || binop.operator().equals("OR")) {
            if (!(lhs instanceof BooleanValue(boolean left))) {
                throw new IllegalArgumentException("Operator '" + binop.operator() + "' requires boolean operands.");
            }

            if (binop.operator().equals("AND") && !left) {
                return new BooleanValue(false);
            }

            if (binop.operator().equals("OR") && left) {
                return new BooleanValue(true);
            }

            RuntimeValue rightOperand = evaluate(binop.RHS(), environment);

            if (!(rightOperand instanceof BooleanValue(boolean right))) {
                throw new IllegalArgumentException("Operator '" + binop.operator() + "' requires boolean operands.");
            }

            return new BooleanValue(right);
        }

        RuntimeValue rhs = evaluate(binop.RHS(), environment);

        if (binop.operator().equals("&")) {
            if (lhs instanceof StringValue(String leftText) && rhs instanceof StringValue(String rightText)) {
                return new StringValue(leftText + rightText);
            }

            throw new IllegalArgumentException("Concatenation requires two STRING values.");
        }

        if (lhs.type() == ValueType.number && rhs.type() == ValueType.number) {
            NumberValue left = (NumberValue) lhs;
            NumberValue right = (NumberValue) rhs;

            return evaluateNumericBinaryExpression(left, right, binop.operator());
        }

        throw new IllegalArgumentException("Operator '" + binop.operator() + "' does not support " + lhs.type() + " and " + rhs.type());

    }

    public static RuntimeValue evaluateNumericBinaryExpression(NumberValue lhs, NumberValue rhs, String operator) {
        return switch(operator) {
            case "+" -> new NumberValue(lhs.value() + rhs.value());
            case "-" -> new NumberValue(lhs.value() - rhs.value());
            case "*" -> new NumberValue(lhs.value() * rhs.value());
            case "/" -> {
                if (rhs.value() == 0.0) {
                    throw new ArithmeticException("Division by zero is not possible.");
                }

                yield new NumberValue(lhs.value() / rhs.value());
            }
            case "^" -> new NumberValue(Math.pow(lhs.value(), rhs.value()));

            case "=" -> new BooleanValue(lhs.value() == rhs.value());
            case "<>" -> new BooleanValue(lhs.value() != rhs.value());
            case "<" -> new BooleanValue(lhs.value() < rhs.value());
            case "<=" -> new BooleanValue(lhs.value() <= rhs.value());
            case ">" -> new BooleanValue(lhs.value() > rhs.value());
            case ">=" -> new BooleanValue(lhs.value() >= rhs.value());

            default -> throw new UnsupportedOperationException("Operator can not be implemeneted into evaluator." + operator);
        };
    }

    public static RuntimeValue evaluateUnaryExpression(UnaryExpression expression, Environment environment) {
        RuntimeValue operand = evaluate(expression.operand(), environment);

        if (expression.operator().equals("NOT")) {
            if (!(operand instanceof BooleanValue(boolean value))) {
                throw new IllegalArgumentException("Unary Operator 'NOT' requires a boolean");
            }
            return new BooleanValue(!value);
        }

        if (!(operand instanceof NumberValue(double value))) {
            throw new IllegalArgumentException("Unary operator '" + expression.operator() + "' requires a number.");
        }

        return switch(expression.operator()) {
          case "+" -> new NumberValue(value);
          case "-" -> new NumberValue(-value);
          default -> throw new UnsupportedOperationException("Unsupported unary operator: " + expression.operator());
        };

    }

    private static String formatOutput(RuntimeValue value) {
        return switch(value) {
            case NumberValue number -> Double.toString(number.value());
            case StringValue text -> text.value();
            case CharValue character -> Character.toString(character.value());
            case BooleanValue bool -> bool.value() ? "TRUE" : "FALSE";

            default -> throw new UnsupportedOperationException("Output formatting not implemented for: " + value.type());
        };
    }

    public static RuntimeValue evaluateOutput(Output output, Environment environment) {
        StringBuilder line = new StringBuilder();

        for (Expression expression : output.values()) {
            RuntimeValue value = evaluate(expression, environment);
            line.append(formatOutput(value));
        }

        System.out.println(line);

        return new NullValue();

    }

}
