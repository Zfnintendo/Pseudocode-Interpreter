import reader.MainReader;
import lexer.MainLexer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Pseudocode Interpreter made by Zeftarium!");

        /* todo
            - make a lexer
            -- do this by making a tokenizer ALMOST
            --- tokenizer enums CHECK
            --- create a token object for each token CHECK
            --- assign the token enum to that object instance CHECK
            --- assigns token data to that instasnce such as the type, value and possibly location? CHECK
            --- adds that to a token list that the parser can interpret later ALMOST
            - make a parser
            - make the interpreter
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

            // parser




        } else {

            throw new IllegalArgumentException("Only one file may be passed into the interpreter at a time (You can still access files from the pseudocode file)");

        }

    }

}