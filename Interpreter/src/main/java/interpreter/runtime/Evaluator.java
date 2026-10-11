package interpreter.runtime;

import lexer.TokenEnums;
import interpreter.values.*;
import ast.*;

import java.util.Scanner;
import java.util.Locale;

public class Evaluator {

    private static final Scanner inputScanner = new Scanner(System.in);

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

            case NodeType.ifStatement -> evaluateIfStatement( (IfStatement) astNode, environment);
            case NodeType.whileStatement -> evaluateWhileStatement( (WhileStatement) astNode, environment);
            case NodeType.repeatStatement -> evaluateRepeatStatement( (RepeatStatement) astNode, environment);
            case NodeType.forStatement -> evaluateForStatement( (ForStatement) astNode, environment);
            case NodeType.caseStatement -> evaluateCaseStatement( (CaseStatement) astNode, environment);

            case NodeType.callExpression -> evaluateCallExpression( (CallExpression) astNode, environment);

            case NodeType.constantDeclaration -> evaluateConstantDeclaration( (ConstantDeclaration) astNode, environment);
            case NodeType.variableDeclaration -> evaluateVariableDeclaration( (VariableDeclaration) astNode, environment);

            case NodeType.identifier -> {
                Identifier identifier = (Identifier) astNode;
                yield environment.read(identifier.symbol());
            }

            case NodeType.assignment -> evaluateAssignment( (Assignment) astNode, environment);

            case NodeType.input -> evaluateInput( (Input) astNode, environment);
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

    private static boolean valuesEqual(RuntimeValue lhs, RuntimeValue rhs) {
        if (lhs instanceof NumberValue(double left) && rhs instanceof NumberValue(double right)) {
            return left == right;
        }
        if (lhs instanceof StringValue(String left) && rhs instanceof StringValue(String right)) {
            return left.equals(right);
        }
        if (lhs instanceof CharValue(char left) && rhs instanceof CharValue(char right)) {
            return left == right;
        }
        if (lhs instanceof BooleanValue(boolean left) && rhs instanceof BooleanValue(boolean right)) {
            return left == right;
        }

        throw new IllegalArgumentException("Can not compare " + lhs.type() + " and " + rhs.type());
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

        if (binop.operator().equals("=") || binop.operator().equals("<>")) {
            boolean equal = valuesEqual(lhs,rhs);
            return new BooleanValue(binop.operator().equals("=") == equal);
        }

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

            case "DIV", "MOD" -> {
                double left = lhs.value();
                double right = rhs.value();

                if (!Double.isFinite(left) || !Double.isFinite(right) || left != Math.rint(left) || right != Math.rint(right)) {
                    throw new IllegalArgumentException(operator + " requires finite integer operands.");
                }

                if (right == 0.0) {
                    throw new ArithmeticException(operator + " can not use a zero divisior");
                }

                if (operator.equals("MOD")) {
                    yield new NumberValue(left % right);
                }

                double quotient = left /right;
                double truncated = quotient < 0 ? Math.ceil(quotient) : Math.floor(quotient);

                yield new NumberValue(truncated);
            }

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

    public static RuntimeValue evaluateInput(Input input, Environment environment) {
        if (!(input.target() instanceof Identifier target)) {
            throw new UnsupportedOperationException("Array input is not implemented yet.");
        }

        if (!(environment.declaredType(target.symbol()) instanceof ScalarType(lexer.TokenEnums tokenType))) {
            throw new UnsupportedOperationException("INPUT requires a scalar variable.");
        }

        if (!inputScanner.hasNextLine()) {
            throw new IllegalStateException("No more input is available.");
        }

        String text = inputScanner.nextLine();

        RuntimeValue value = switch(tokenType) {
            case TokenEnums.typeInteger, TokenEnums.typeReal -> {
                double number;

                try {
                    number = Double.parseDouble(text.trim());
                } catch(NumberFormatException exception) {
                    throw new IllegalArgumentException("Expected numeric input, got: " + text, exception);
                }

                if (!Double.isFinite(number)) {
                    throw new IllegalArgumentException("Input must be a finite number.");
                }

                yield new NumberValue(number);
            }
            case TokenEnums.typeString -> new StringValue(text);
            case TokenEnums.typeChar -> {
                if (text.length() != 1 ) {
                    throw new IllegalArgumentException("CHAR input must contain exactly one character.");
                }

                yield new CharValue(text.charAt(0));
            }
            case TokenEnums.typeBoolean -> {
                String booleanText = text.trim();

                if (booleanText.equalsIgnoreCase("TRUE")) {
                    yield new BooleanValue(true);
                }
                if (booleanText.equalsIgnoreCase("FALSE")) {
                    yield new BooleanValue(false);
                }

                throw new IllegalArgumentException("BOOLEAN input must be TRUE or FALSE.");
            }

            default -> throw new UnsupportedOperationException("Unsupported input type: " + tokenType);
        };

        environment.assign(target.symbol(), value);
        return new NullValue();
    }

    public static RuntimeValue evaluateIfStatement(IfStatement statement, Environment environment) {
        RuntimeValue condition = evaluate(statement.condition(), environment);

        if (!(condition instanceof BooleanValue(boolean result))) {
            throw new IllegalArgumentException("IF condition must be a boolean.");
        }

        var body = result ? statement.thenBody() : statement.elseBody();

        for (Statement bodyStatement : body) {
            evaluate(bodyStatement, environment);
        }

        return new NullValue();
    }

    public static RuntimeValue evaluateWhileStatement(WhileStatement statement, Environment environment) {

        while (true) {
            RuntimeValue condition = evaluate(statement.condition(), environment);

            if (!(condition instanceof BooleanValue(boolean result))) {
                throw new IllegalArgumentException("WHILE condition must be a boolean.");
            }

            if (!result) {
                break;
            }

            for (Statement bodyStatement : statement.body()) {
                evaluate(bodyStatement, environment);
            }
        }

        return new NullValue();

    }

    public static RuntimeValue evaluateRepeatStatement(RepeatStatement statement, Environment environment) {

        while (true) {
            for (Statement bodyStatement : statement.body()) {
                evaluate(bodyStatement, environment);
            }

            RuntimeValue condition = evaluate(statement.condition(), environment);

            if (!(condition instanceof BooleanValue(boolean result))) {
                throw new IllegalArgumentException("UNTIL condition must be a boolean.");
            }

            if (result) {
                break;
            }

        }

        return new NullValue();

    }

    public static RuntimeValue evaluateForStatement(ForStatement statement, Environment environment) {

        RuntimeValue startValue = evaluate(statement.start(), environment);
        RuntimeValue endValue = evaluate(statement.end(), environment);
        RuntimeValue stepValue = evaluate(statement.step(), environment);

        if (!(startValue instanceof NumberValue(double start))
                || !(endValue instanceof NumberValue(double end))
                || !(stepValue instanceof NumberValue(double step))) {
            throw new IllegalArgumentException("FOR start, end and step must be numbers.");
        }

        if (!Double.isFinite(start) || !Double.isFinite(end) || !Double.isFinite(step) || step == 0.0) {
            throw new IllegalArgumentException("FOR bounds and step must be finite and step must not be 0.");
        }

        String variableName = statement.variable().symbol();
        double counter = start;

        environment.assign(variableName, new NumberValue(counter));

        while (step > 0 ? counter <= end : counter >= end) {
            for (Statement bodyStatement : statement.body()) {
                evaluate(bodyStatement, environment);
            }

            counter += step;
            environment.assign(variableName, new NumberValue(counter));
        }

        return new NullValue();

    }

    public static RuntimeValue evaluateCaseStatement(CaseStatement statement, Environment environment) {
        RuntimeValue selector = evaluate(statement.selector(), environment);

        for (Cases branch : statement.cases()) {
            RuntimeValue caseValue = evaluate(branch.value(), environment);

            if (valuesEqual(selector, caseValue)) {
                evaluate(branch.statement(), environment);
                return new NullValue();
            }
        }

        if (statement.otherwiseCase().isPresent()) {
            evaluate(statement.otherwiseCase().get(), environment);
        }

        return new NullValue();
    }

    public static RuntimeValue evaluateVariableDeclaration(VariableDeclaration declaration, Environment environment) {

        if (declaration.declaredType() instanceof ArrayType arrayType) {
            int dimensions = arrayType.bounds().size();

            if (dimensions < 1 || dimensions > 2 || !(arrayType.elementType() instanceof ScalarType elementType)) {
                throw new UnsupportedOperationException("Only one dimensional or two dimensional arrays are supported.");
            }

            int[] lower = new int[dimensions];
            int[] upper = new int[dimensions];

            for (int i = 0; i < dimensions; i++) {
                ArrayBounds bounds = arrayType.bounds().get(i);

                lower[i] = evaluateArrayInteger(bounds.lower(), environment);
                upper[i] = evaluateArrayInteger(bounds.upper(), environment);
            }

            ArrayValue array = new ArrayValue(elementType, lower, upper);

            environment.declareVariable(declaration.name(), arrayType, array);

            return new NullValue();
        }

        if (!(declaration.declaredType() instanceof ScalarType type)) {
            throw new UnsupportedOperationException("Unsupported declaration type.");
        }

        switch(type.tokenType()) {
            case typeInteger,typeReal, typeString, typeChar, typeBoolean -> environment.declareVariable(declaration.name(), type);

            default -> throw new UnsupportedOperationException("Unsupported scalar type: " + type.tokenType());
        }

        return new NullValue();
    }

    private static int evaluateArrayInteger(Expression expression, Environment environment) {
        RuntimeValue value = evaluate(expression, environment);

        if (!(value instanceof NumberValue(double number))
                || !Double.isFinite(number)
                || number != Math.rint(number)
                || number < Integer.MIN_VALUE
                || number > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Array bounds and indices must be integers in the proper integer range.");
        }

        return (int) number;
    }

    public static RuntimeValue evaluateConstantDeclaration(ConstantDeclaration declaration, Environment environment) {
        RuntimeValue value = evaluate(declaration.value(), environment);

        TokenEnums tokenType = switch(value) {
            case NumberValue(double number) -> {
                if (!Double.isFinite(number)) {
                    throw new IllegalArgumentException("Numeric constants must be finite.");
                }

                yield number == Math.rint(number) ? TokenEnums.typeInteger : TokenEnums.typeReal;
            }
            case StringValue _ -> TokenEnums.typeString;
            case CharValue _ -> TokenEnums.typeChar;
            case BooleanValue _ -> TokenEnums.typeBoolean;

            default -> throw new UnsupportedOperationException("Unsupported constant value: " + value.type());
        };

        environment.declareConstant(declaration.name(), new ScalarType(tokenType), value);

        return new NullValue();
    }

    public static RuntimeValue evaluateAssignment(Assignment assignment, Environment environment) {
        if (!(assignment.target() instanceof Identifier target)) {
            throw new UnsupportedOperationException("Array assignment is not implmented yet.");
        }

        RuntimeValue value = evaluate(assignment.value(), environment);

        environment.assign(target.symbol(), value);

        return new NullValue();
    }

    public static RuntimeValue evaluateCallExpression(CallExpression call, Environment environment) {
        return switch(call.name()) {
            case "LENGTH", "UCASE", "LCASE" -> {
                if (call.arguments().size() != 1) {
                    throw new IllegalArgumentException(call.name() + " requires exactly one argument.");
                }

                RuntimeValue argument = evaluate(call.arguments().getFirst(), environment);

                if (!(argument instanceof StringValue(String text))) {
                    throw new IllegalArgumentException(call.name() + " requires a STRING argument.");
                }

                yield switch(call.name()) {
                    case "LENGTH" -> new NumberValue(text.length());
                    case "UCASE" -> new StringValue(text.toUpperCase(Locale.ROOT));
                    case "LCASE" -> new StringValue(text.toLowerCase(Locale.ROOT));

                    default -> throw new IllegalStateException("Unexpected string function: " + call.name());
                };
            }
            case "SUBSTRING" -> {
                if (call.arguments().size() != 3) {
                    throw new IllegalArgumentException("SUBSTRING requires exactly 3 arguments.");
                }

                RuntimeValue textValue = evaluate(call.arguments().getFirst(), environment);
                RuntimeValue startValue = evaluate(call.arguments().get(1), environment);
                RuntimeValue lengthValue = evaluate(call.arguments().get(2), environment);

                if (!(textValue instanceof StringValue(String text))
                        || !(startValue instanceof NumberValue(double start))
                        || !(lengthValue instanceof NumberValue(double length))) {
                    throw new IllegalArgumentException("SUBSTRING requires a STRING followed by two indicies.");
                }

                int fromIndex = getFromIndex(text, start, length);
                int toIndex = fromIndex + (int) length;

                yield new StringValue(text.substring(fromIndex, toIndex));
            }

            default -> throw new UnsupportedOperationException("Function is not implemented: " + call.name());
        };
    }

    private static int getFromIndex(String text, double start, double length) {
        if (!Double.isFinite(start) || !Double.isFinite(length) || start != Math.rint(start) || length != Math.rint(length)) {
            throw new IllegalArgumentException("SUBSTRING start and length must be finite integers.");
        }

        double startIndex = start - 1;

        if (startIndex < 0 || startIndex > text.length() || length < 0 || length > text.length() - startIndex) {
            throw new IllegalArgumentException("SUBTRING range is outside the string.");
        }

        return (int) startIndex;
    }
}
