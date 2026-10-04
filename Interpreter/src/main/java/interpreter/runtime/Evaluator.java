package interpreter.runtime;

import interpreter.values.ValueType;
import interpreter.values.RuntimeValue;
import interpreter.values.NumberValue;

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

    public static RuntimeValue evaluateBinaryExpression(BinaryExpression binop) {

    }

    public static RuntimeValue evaluateProgram(Program program) {
        RuntimeValue lastEvaluated = new RuntimeValue
    }

}
