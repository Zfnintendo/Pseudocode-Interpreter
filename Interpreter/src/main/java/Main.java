import reader.MainReader;
import lexer.MainLexer;
import parser.MainParser;
import parser.Program;
import evaluator.AstPrinter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Pseudocode Interpreter made by Zeftarium!");

        /* todo
            - make a lexer CHECK
            - make a parser ?
            - make the interpreter
            - WORK ON README FOR DOCUMENTATION PURPOSES!
         */

        // start of main

        MainReader mainReader = new MainReader(args);

        // no need to check if there are any args, main reader already throws an error for that

        if (args.length == 1) {

            // reader
            File file = mainReader.getFile();

            // lexer
            MainLexer mainLexer = new MainLexer(file);
            ArrayList<ArrayList<Object>> lexedFile = mainLexer.lexFile();

            System.out.println(lexedFile);

            // parser

            MainParser mainParser = new MainParser();
            Program abstractSyntaxTree = mainParser.produceAST(lexedFile);

            System.out.println(abstractSyntaxTree);

            AstPrinter.printTree(abstractSyntaxTree);




        } else {

            throw new IllegalArgumentException("Only one file may be passed into the interpreter at a time (You can still access files from the pseudocode file)");

        }

    }

}