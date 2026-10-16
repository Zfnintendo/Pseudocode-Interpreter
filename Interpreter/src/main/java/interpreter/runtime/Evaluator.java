package interpreter.runtime;

import lexer.TokenEnums;
import interpreter.values.*;
import ast.*;

import java.util.Scanner;
import java.util.Locale;
import java.util.function.Consumer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;

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
            case NodeType.indexExpression -> evaluateIndexExpression( (IndexExpression) astNode, environment);

            case NodeType.ifStatement -> evaluateIfStatement( (IfStatement) astNode, environment);
            case NodeType.whileStatement -> evaluateWhileStatement( (WhileStatement) astNode, environment);
            case NodeType.repeatStatement -> evaluateRepeatStatement( (RepeatStatement) astNode, environment);
            case NodeType.forStatement -> evaluateForStatement( (ForStatement) astNode, environment);
            case NodeType.caseStatement -> evaluateCaseStatement( (CaseStatement) astNode, environment);
            case NodeType.callStatement -> evaluateCallStatement(  (CallStatement) astNode, environment);
            case NodeType.returnStatement -> evaluateReturnStatement( (ReturnStatement) astNode, environment);

            case NodeType.openFileStatement -> evaluateOpenFileStatement( (OpenFileStatement) astNode, environment);
            case NodeType.closeFileStatement -> evaluateCloseFileStatement( (CloseFileStatement) astNode, environment);
            case NodeType.writeFileStatement -> evaluateWriteFileStatement( (WriteFileStatement) astNode, environment);
            case NodeType.readFileStatement -> evaluateReadFileStatement( (ReadFileStatement) astNode, environment);

            case NodeType.callExpression -> evaluateCallExpression( (CallExpression) astNode, environment);

            case NodeType.constantDeclaration -> evaluateConstantDeclaration( (ConstantDeclaration) astNode, environment);
            case NodeType.variableDeclaration -> evaluateVariableDeclaration( (VariableDeclaration) astNode, environment);
            case NodeType.functionDeclaration -> evaluateFunctionDeclaration( (FunctionDeclaration) astNode, environment);
            case NodeType.procedureDeclaration -> evaluateProcedureDeclaration( (ProcedureDeclaration) astNode, environment);

            case NodeType.identifier -> evaluateIdentifier( (Identifier) astNode, environment);

            case NodeType.assignment -> evaluateAssignment( (Assignment) astNode, environment);

            case NodeType.input -> evaluateInput( (Input) astNode, environment);
            case NodeType.output -> evaluateOutput( (Output) astNode, environment );

            default -> throw new UnsupportedOperationException("This AST node has not been set up for interpretation: " + astNode.kind());
        };

    }

    public static RuntimeValue evaluateProgram(Program program, Environment environment) {
        RuntimeValue lastEvaluated = new NullValue();

        try {
            for (Statement statement : program.body()) {
                lastEvaluated = evaluate(statement, environment);
            }
        } catch (ReturnSignal signal) {
            throw new IllegalStateException("RETURN is only allowed inside a function.");
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

    public static RuntimeValue evaluateIndexExpression(IndexExpression expression, Environment environment) {
        ArrayValue array = evaluateArrayBase(expression, environment);
        int[] indices = evaluateArrayIndices(expression, environment);

        return array.read(indices);
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
        TokenEnums tokenType;
        Consumer<RuntimeValue> writeValue;

        if (input.target() instanceof Identifier target) {
            if (!(environment.declaredType(target.symbol()) instanceof ScalarType(TokenEnums type))) {
                throw new UnsupportedOperationException("INPUT requires a scalar variable or an indexed array element.");
            }

            tokenType = type;
            writeValue = value -> environment.assign(target.symbol(), value);
        } else if (input.target() instanceof IndexExpression target) {
            ArrayValue array = evaluateArrayBase(target, environment);
            int[] indices = evaluateArrayIndices(target, environment);

            array.validateIndices(indices);

            tokenType = array.elementType().tokenType();
            writeValue = value -> array.assign(value, indices);
        } else {
            throw new UnsupportedOperationException("Unsupported INPUT target: " + input.target().kind());
        }

        if (!inputScanner.hasNextLine()) {
            throw new IllegalStateException("No more input is available.");
        }

        String text = inputScanner.nextLine();

        RuntimeValue value = parseInputValue(text, tokenType);

        writeValue.accept(value);
        return new NullValue();
    }

    private static RuntimeValue parseInputValue(String text, TokenEnums tokenType) {
        return switch(tokenType) {
            case TokenEnums.typeInteger, TokenEnums.typeReal -> {
                double number;

                try {
                    number = Double.parseDouble(text.trim());
                } catch (NumberFormatException exception) {
                    throw new IllegalArgumentException("Expected numeric input, got: " + text, exception);
                }

                if (!Double.isFinite(number)) {
                    throw new IllegalArgumentException("Input must be a finite number.");
                }

                yield new NumberValue(number);
            }
            case TokenEnums.typeString -> new StringValue(text);
            case TokenEnums.typeChar -> {
                if (text.length() != 1) {
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

    public static RuntimeValue evaluateCallStatement(CallStatement statement, Environment environment) {
        CallExpression call = statement.call();

        ProcedureEntry entry = environment.lookupProcedure(call.name());
        ProcedureDeclaration declaration = entry.declaration();

        var parameters = declaration.parameters();
        var arguments = call.arguments();

        if (arguments.size() != parameters.size()) {
            throw new IllegalArgumentException(call.name() + " requires " + parameters.size() + " arguments, got " + arguments.size());
        }

        RuntimeValue[] values =  new RuntimeValue[arguments.size()];

        for (int i = 0; i < arguments.size(); i++) {
            values[i] = evaluate(arguments.get(i), environment);
        }

        Environment local = new Environment(entry.declarationEnvironment());

        for (int i = 0; i < parameters.size(); i++) {
            Parameter parameter = parameters.get(i);
            RuntimeValue value = values[i];

            if (parameter.type() instanceof ArrayType arrayType) {
                validateArrayParameter(parameter, arrayType, value, local);
            }

            local.declareVariable(parameter.name(), parameter.type(), value);
        }

        try {
            for (Statement bodyStatement : declaration.body()) {
                evaluate(bodyStatement, local);
            }
        } catch (ReturnSignal signal) {
            throw new IllegalStateException("RETURN with a value is not allowed in a procedure.");
        }

        return new NullValue();
    }

    public static RuntimeValue evaluateReturnStatement(ReturnStatement statement, Environment environment) {
        RuntimeValue value = evaluate(statement.value(), environment);
        throw new ReturnSignal(value);
    }

    public static RuntimeValue evaluateUserFunction(CallExpression call, Environment environment) {
        FunctionEntry entry = environment.lookupFunction(call.name());
        FunctionDeclaration declaration = entry.declaration();

        if (!(declaration.returnType() instanceof ScalarType returnType)) {
            throw new UnsupportedOperationException("Only scalar function return types are supported currently.");
        }

        var parameters = declaration.parameters();
        var arguments = call.arguments();

        if (arguments.size() != parameters.size()) {
            throw new IllegalArgumentException(call.name() + " requires " + parameters.size() + " arguments, got " + arguments.size());
        }

        RuntimeValue[] values = new RuntimeValue[arguments.size()];

        for (int i = 0; i < arguments.size(); i++) {
            values[i] = evaluate(arguments.get(i), environment);
        }

        Environment local = new Environment(entry.declarationEnvironment());

        for (int i = 0; i < parameters.size(); i++) {
            Parameter parameter = parameters.get(i);
            RuntimeValue value = values[i];

            if (parameter.type() instanceof ArrayType arrayType) {
                validateArrayParameter(parameter, arrayType, value, local);
            }

            local.declareVariable(parameter.name(), parameter.type(), value);
        }

        try {
            for (Statement bodyStatement : declaration.body()) {
                evaluate(bodyStatement, local);
            }
        } catch (ReturnSignal signal) {
            VariableEntry result = new VariableEntry(returnType, signal.value(), false);

            return result.value();
        }

        throw new IllegalStateException("Function '"  + call.name() + "' finished without RETURN.");
    }

    private static void validateArrayParameter(Parameter parameter, ArrayType expected, RuntimeValue value, Environment local) {
        if (!(value instanceof ArrayValue array)) {
            throw new IllegalArgumentException("Parameter '" + parameter.name() + "' requires an array.");
        }

        int dimensions = expected.bounds().size();

        if (dimensions < 1 || dimensions > 2 || !(expected.elementType() instanceof ScalarType elementType)) {
            throw new UnsupportedOperationException("Only one dimensional or two dimensional arrays are supported currently.");
        }

        if (array.dimensions() != dimensions || !array.elementType().equals(elementType)) {
            throw new IllegalArgumentException("Array dimensions or element type do not match parameter '" + parameter.name() + "'.");
        }

        for (int i = 0; i < dimensions; i++) {
            ArrayBounds bounds = expected.bounds().get(i);

            int lower = evaluateArrayInteger(bounds.lower(), local);
            int upper = evaluateArrayInteger(bounds.upper(), local);

            if (lower != array.lowerBound(i) || upper != array.upperBound(i)) {
                throw new IllegalArgumentException("Array bounds do not match parameter '" + parameter.name() + "' in dimensions " + (i + 1));
            }
        }
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

    private static int[] evaluateArrayIndices(IndexExpression expression, Environment environment) {
        int[] indices = new int[expression.indices().size()];

        for (int i = 0; i < indices.length; i++) {
            indices[i] = evaluateArrayInteger(expression.indices().get(i), environment);
        }

        return indices;
    }

    private static ArrayValue evaluateArrayBase(IndexExpression expression, Environment environment) {
        RuntimeValue base = evaluate(expression.base(), environment);

        if (!(base instanceof ArrayValue array)) {
            throw new IllegalArgumentException("Indexing requires an array.");
        }

        return array;
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

    public static RuntimeValue evaluateFunctionDeclaration(FunctionDeclaration declaration, Environment environment) {
        environment.declareFunction(declaration);
        return new NullValue();
    }

    public static RuntimeValue evaluateProcedureDeclaration(ProcedureDeclaration declaration, Environment environment) {
        environment.declareProcedure(declaration);
        return new NullValue();
    }

    public static RuntimeValue evaluateAssignment(Assignment assignment, Environment environment) {
        if (assignment.target() instanceof Identifier target) {
            RuntimeValue value = evaluate(assignment.value(), environment);
            environment.assign(target.symbol(), value);

            return new NullValue();
        }

        if (assignment.target() instanceof IndexExpression target) {
            ArrayValue array = evaluateArrayBase(target, environment);
            int[] indices = evaluateArrayIndices(target, environment);
            RuntimeValue value = evaluate(assignment.value(), environment);

            array.assign(value, indices);

            return new NullValue();
        }

        throw new UnsupportedOperationException("Unsupported assignment target: " + assignment.target().kind());
    }

    public static RuntimeValue evaluateIdentifier(Identifier identifier, Environment environment) {
        return environment.read(identifier.symbol());
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

            case "ROUND" -> {
                requireArgumentCount(call, 2);

                RuntimeValue numberValue = evaluate(call.arguments().getFirst(), environment);
                RuntimeValue placesValue = evaluate(call.arguments().get(1), environment);

                if (!(numberValue instanceof NumberValue(double number)) || !(placesValue instanceof NumberValue(double places))) {
                    throw new IllegalArgumentException("ROUND requires a number and an integer decimal place count.");
                }

                if (!Double.isFinite(number)) {
                    throw new IllegalArgumentException("ROUND requires a finite number.");
                }

                if (!Double.isFinite(places) || places != Math.rint(places) || places < 0 || places > Integer.MAX_VALUE) {
                    throw new IllegalArgumentException("ROUND places must be a non negative integer in the appropriate range.");
                }

                if (places >= 324) {
                    yield new NumberValue(number);
                }

                double rounded = BigDecimal.valueOf(number).setScale((int) places, RoundingMode.HALF_UP).doubleValue();

                yield new NumberValue(rounded);
            }
            case "RANDOM" -> {
                requireArgumentCount(call, 0);

                long steps = 1L << 53;
                long sample = ThreadLocalRandom.current().nextLong(steps + 1);

                yield new NumberValue(sample / (double) steps);
            }
            case "DIV", "MOD" -> {
                requireArgumentCount(call, 2);

                RuntimeValue leftValue = evaluate(call.arguments().getFirst(), environment);
                RuntimeValue rightValue = evaluate(call.arguments().get(1), environment);

                if (!(leftValue instanceof NumberValue left) || !(rightValue instanceof NumberValue right)) {
                    throw new IllegalArgumentException(call.name() + " requires two numeric arguments.");
                }

                yield evaluateNumericBinaryExpression(left, right, call.name());
            }

            default -> evaluateUserFunction(call, environment);
        };
    }

    private static void requireArgumentCount(CallExpression call, int expected) {
        if (call.arguments().size() != expected) {
            throw new IllegalArgumentException(call.name() + " requires " + expected + " arguments, got " + call.arguments().size());
        }
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

    public static RuntimeValue evaluateOpenFileStatement(OpenFileStatement statement, Environment environment) {
        String filename = evaluateFilename(statement.filename(), environment);

        environment.files().open(filename, statement.mode());

        return new NullValue();
    }

    public static RuntimeValue evaluateCloseFileStatement(CloseFileStatement statement, Environment environment) {
        String filename = evaluateFilename(statement.filename(), environment);

        environment.files().close(filename);

        return new NullValue();
    }

    public static RuntimeValue evaluateWriteFileStatement(WriteFileStatement statement, Environment environment) {
        String filename = evaluateFilename(statement.filename(), environment);

        RuntimeValue value = evaluate(statement.value(), environment);
        String text = formatOutput(value);

        environment.files().writeLine(filename, text);

        return new NullValue();
    }

    public static RuntimeValue evaluateReadFileStatement(ReadFileStatement statement, Environment environment) {
        String filename = evaluateFilename(statement.filename(), environment);

        TokenEnums tokenType;
        Consumer<RuntimeValue> writeValue;

        if (statement.target() instanceof Identifier target) {
            if (!(environment.declaredType(target.symbol()) instanceof ScalarType(TokenEnums type))) {
                throw new UnsupportedOperationException("READFILE requires a scalar variable or an indexed array element.");
            }

            tokenType = type;
            writeValue = value -> environment.assign(target.symbol(), value);
        } else if (statement.target() instanceof IndexExpression target) {
            ArrayValue array = evaluateArrayBase(target, environment);
            int[] indices = evaluateArrayIndices(target, environment);

            array.validateIndices(indices);

            tokenType = array.elementType().tokenType();
            writeValue = value -> array.assign(value, indices);
        } else {
            throw new UnsupportedOperationException("Unsupported READFILE target: " + statement.target().kind());
        }

        String text = environment.files().readLine(filename);
        RuntimeValue value = parseInputValue(text, tokenType);

        writeValue.accept(value);

        return new NullValue();
    }

    private static String evaluateFilename(Expression expression, Environment environment) {
        RuntimeValue value = evaluate(expression, environment);

        if (!(value instanceof StringValue(String filename))) {
            throw new IllegalArgumentException("Filename must be a STRING.");
        }

        return filename;
    }
}
