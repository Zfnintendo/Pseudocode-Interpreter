package interpreter;

import ast.Program;
import interpreter.runtime.Environment;
import interpreter.runtime.Evaluator;
import interpreter.runtime.FileManager;
import interpreter.values.RuntimeValue;

public class MainInterpreter {

    public MainInterpreter() {}

    public static RuntimeValue interpret(Program program) {
        Environment environment = new Environment();

        try (FileManager files = environment.files()) {
            return Evaluator.evaluate(program, environment);
        }
    }


}
