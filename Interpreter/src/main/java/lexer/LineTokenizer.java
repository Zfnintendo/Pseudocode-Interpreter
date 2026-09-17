package lexer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

// use this class by creating a new instance of it then calling tokenize to get the table of tokens

/*future todo when not lazy
          - create a function or method for every string builder
          - make a recursive function for identifiers and literals
          -- could do this by having after the first blank character check to check for future characters and find a " " to stop it and then reoccur the function to find if there are any tokens and then
 */

public class LineTokenizer {

    private String line;
    private int lineNumber;

    // apparently this is O(1) lookup so we do it this way
    private static final boolean[] endChar = new boolean[128];
    Set<String> keywordStrings = new HashSet<>();

    static {
        endChar[' '] =  true;
        endChar['+'] = true;
        endChar['-'] = true;
        endChar['*'] = true;
        endChar['/'] = true;
        endChar['^'] = true;
        endChar['='] = true;
        endChar['<'] = true;
        endChar['>'] = true;
        endChar['('] = true;
        endChar[')'] = true;
        endChar['['] = true;
        endChar[']'] = true;
        endChar[':'] = true;
        endChar[','] = true;

    }

    public LineTokenizer(String line, int lineNumber) {
        this.line = line;
        this.lineNumber = lineNumber;
    }

    public ArrayList<ArrayList<Object>> tokenize() {
        AddToken addToken = new AddToken();

        for (int i = 0; i < this.line.length(); i++) {
            TokenLocation tokenLocation = new TokenLocation(this.lineNumber, i);

            char c0 = this.line.charAt(i);
            switch(c0) { // check for symbol tokens or 1 character tokens | ONE CHARACTER STARTS HERE!
                // i know this might not be optimal because we have endChar and we could do something about that but I'm lazy so we are not doing that
                case ' ': {
                    break; // do nothing
                }
                case '+': {
                    Token token = new Token(TokenType.operator, "+", tokenLocation);
                    addToken.addToLine(token, TokenEnums.addition);
                    break;
                }
                case '-': {
                    Token token = new Token(TokenType.operator, "-", tokenLocation);
                    addToken.addToLine(token, TokenEnums.subtraction);
                    break;
                }
                case '*': {
                    Token token = new Token(TokenType.operator, "*", tokenLocation);
                    addToken.addToLine(token, TokenEnums.multiplication);
                    break;
                }
                case '/': {
                    Token token = new Token(TokenType.operator, "/", tokenLocation);
                    addToken.addToLine(token, TokenEnums.division);
                    break;
                }
                case '^': {
                    Token token = new Token(TokenType.operator, "^", tokenLocation);
                    addToken.addToLine(token, TokenEnums.exponent);
                    break;
                }
                case '=': {
                    Token token = new Token(TokenType.operator, "=", tokenLocation);
                    addToken.addToLine(token, TokenEnums.equals);
                    break;
                }
                case '<': {
                    Token token = new Token(TokenType.operator, "<", tokenLocation);
                    addToken.addToLine(token, TokenEnums.lessThan);
                    break;
                }
                case '>': {
                    Token token = new Token(TokenType.operator, ">", tokenLocation);
                    addToken.addToLine(token, TokenEnums.greaterThan);
                    break;
                }
                case '(': {
                    Token token = new Token(TokenType.punctuation, "(", tokenLocation);
                    addToken.addToLine(token, TokenEnums.leftParen);
                    break;
                }
                case ')': {
                    Token token = new Token(TokenType.punctuation, ")", tokenLocation);
                    addToken.addToLine(token, TokenEnums.rightParen);
                    break;
                }
                case '[': {
                    Token token = new Token(TokenType.punctuation, "[", tokenLocation);
                    addToken.addToLine(token, TokenEnums.leftBracket);
                    break;
                }
                case ']': {
                    Token token = new Token(TokenType.punctuation, "]", tokenLocation);
                    addToken.addToLine(token, TokenEnums.rightBracket);
                    break;
                }
                case '"': {
                    Token token = new Token(TokenType.punctuation, "\"", tokenLocation);
                    addToken.addToLine(token, TokenEnums.doubleQuote);

                    // build string literal
                    StringBuilder sb = new StringBuilder();
                    i++;

                    while ( this.line.charAt(i) != '"' ) {

                        sb.append(this.line.charAt(i));
                        i++;

                        if (i >= this.line.length()) { break; }// replace with throw error message later as this is when its longer than the line therefore error
                    }

                    tokenLocation.changeColumn(i);
                    Token stringLiteralToken = new Token(TokenType.literals, sb.toString(), tokenLocation);
                    addToken.addToLine(stringLiteralToken, TokenEnums.literalString);

                    break;
                }
                case '\'': {
                    Token token = new Token(TokenType.punctuation, "'", tokenLocation);
                    addToken.addToLine(token, TokenEnums.singleQuote);

                    if ( this.line.charAt(i+2) != '\'') { break; }// replace with throw error because expected '

                    i++;

                    tokenLocation.changeColumn(i);

                    Token charLiteralToken = new Token(TokenType.literals, String.valueOf(this.line.charAt(i)), tokenLocation);
                    addToken.addToLine(charLiteralToken, TokenEnums.literalChar);

                    break;
                }
                case ',': {
                    Token token = new Token(TokenType.punctuation, ",", tokenLocation);
                    addToken.addToLine(token, TokenEnums.comma);
                    break;
                }
                case ':': {
                    Token token = new Token(TokenType.punctuation, ":", tokenLocation);
                    addToken.addToLine(token, TokenEnums.colon);
                    break;
                }
                default: {/*
                    "I assume keywords are a subset of identifiers.

                    You should not rely on the lexer to find keywords. Instead your lexer should only look for identifiers, greedily, i.e. it should match the longest sequence of characters that constitutes an identifier.

                    When it finds one, you should check yourself if the text of the identifier is one of the keywords. If it is, return a KEYWORD token, else return an IDENTIFIER token."

                    -- some dude on stack overflow
                    */

                    // we check for integer and real literals

                    boolean isReal = false;
                    boolean isInt = false;

                    StringBuilder sb = new StringBuilder();
                    while ( Character.isDigit(this.line.charAt(i)) ) {
                        sb.append(this.line.charAt(i));
                        i++;
                        isInt = true;
                    }

                    if (this.line.charAt(i) == '.') {
                        i++;
                        while ( Character.isDigit(this.line.charAt(i)) ) {
                            sb.append(this.line.charAt(i));
                            i++;
                        }
                        isInt = false;
                        isReal = true;
                    }

                    if (isInt) {
                        Token token = new Token(TokenType.literals, sb.toString(), tokenLocation);
                        addToken.addToLine(token, TokenEnums.literalInteger);
                        break;
                    } else if (isReal) {
                        Token token = new Token(TokenType.literals, sb.toString(), tokenLocation);
                        addToken.addToLine(token, TokenEnums.literalReal);
                        break;
                    }

                    // end of check for int and real literals


                    // check for a terminating symbol or character to recognise an identifier which is a character
                    if ( this.line.charAt(i+1) < 128 && endChar[this.line.charAt(i+1)]) {
                        Token token = new Token(TokenType.identifier, String.valueOf(this.line.charAt(i)), tokenLocation);
                        addToken.addToLine(token, TokenEnums.identifier);
                        break;
                    }


                }
            } // end of first switch


            if (i + 1 >= this.line.length()) { continue; } // check if i+1 exists in the string


            // start of second switch
            char c1 = this.line.charAt(i+1);
            String stringA = String.valueOf(new char[]{c0, c1});

            switch(stringA) { // checks for two character tokens
                case "<-": {
                    Token token = new Token(TokenType.operator, "<-", tokenLocation);
                    addToken.addToLine(token, TokenEnums.assignment);
                    i++; // we skip the next iteration as thats basically the same token
                    break;
                }
                case "OR": {
                    Token token = new Token(TokenType.operator, "OR", tokenLocation);
                    addToken.addToLine(token, TokenEnums.or);
                    i++;
                    break;
                }
                case "<=": {
                    Token token = new Token(TokenType.operator, "<=", tokenLocation);
                    addToken.addToLine(token, TokenEnums.lessEqualTo);
                    i++;
                    break;
                }
                case ">=": {
                    Token token = new Token(TokenType.operator, ">=", tokenLocation);
                    addToken.addToLine(token, TokenEnums.greaterEqualTo);
                    i++;
                    break;
                }
                case "<>": {
                    Token token = new Token(TokenType.operator, "<>", tokenLocation);
                    addToken.addToLine(token, TokenEnums.notEqualTo);
                    i++;
                    break;
                }
                case "OF": {
                    Token token = new Token(TokenType.keyword, "OF", tokenLocation);
                    addToken.addToLine(token, TokenEnums.of);
                    i++;
                    break;
                }
                case "IF": {
                    Token token = new Token(TokenType.keyword, "IF", tokenLocation);
                    addToken.addToLine(token, TokenEnums.tokenIf);
                    i++;
                    break;
                }
            } // end of second switch


            if (i+2 >= this.line.length()) { continue; }


            // Start of third switch
            char c2 = this.line.charAt(i+2);
            StringBuilder sb0 = new StringBuilder(stringA);
            sb0.append(c2);
            switch(sb0.toString()) {
                case "DIV": {
                    Token token = new Token(TokenType.keyword, "DIV", tokenLocation);
                    addToken.addToLine(token, TokenEnums.div);
                    i += 2;
                    break;
                }
                case "MOD": {
                    Token token = new Token(TokenType.keyword, "MOD", tokenLocation);
                    addToken.addToLine(token, TokenEnums.mod);
                    i += 2;
                    break;
                }
                case "AND": {
                    Token token = new Token(TokenType.operator, "AND", tokenLocation);
                    addToken.addToLine(token, TokenEnums.and);
                    i += 2;
                    break;
                }
                case "NOT": {
                    Token token = new Token(TokenType.operator, "NOT", tokenLocation);
                    addToken.addToLine(token, TokenEnums.not);
                    i += 2;
                    break;
                }
                case "FOR": {
                    Token token = new Token(TokenType.keyword, "FOR", tokenLocation);
                    addToken.addToLine(token, TokenEnums.tokenFor);
                    i += 2;
                    break;
                }
            } // end of third switch


            if (i+3 >= this.line.length()) { continue; }


            // Start of fourth switch
            char c3 = this.line.charAt(i+3);
            sb0.append(c3);
            switch(sb0.toString()) {
                case "REAL": {
                    Token token = new Token(TokenType.keyword, "REAL", tokenLocation);
                    addToken.addToLine(token, TokenEnums.typeReal);
                    i += 3;
                    break;
                }
                case "CHAR": {
                    Token token = new Token(TokenType.keyword, "CHAR", tokenLocation);
                    addToken.addToLine(token, TokenEnums.typeChar);
                    i += 3;
                    break;
                }
                case "CALL": {
                    Token token = new Token(TokenType.keyword, "CALL", tokenLocation);
                    addToken.addToLine(token, TokenEnums.call);
                    i += 3;
                    break;
                }
                case "THEN": {
                    Token token = new Token(TokenType.keyword, "THEN", tokenLocation);
                    addToken.addToLine(token, TokenEnums.then);
                    i += 3;
                    break;
                }
                case "ELSE": {
                    Token token = new Token(TokenType.keyword, "ELSE", tokenLocation);
                    addToken.addToLine(token, TokenEnums.tokenElse);
                    i += 3;
                    break;
                }
                case "NEXT": {
                    Token token = new Token(TokenType.keyword, "NEXT", tokenLocation);
                    addToken.addToLine(token, TokenEnums.next);
                    i += 3;
                    break;
                }
                case "CASE": {
                    Token token = new Token(TokenType.keyword, "CASE", tokenLocation);
                    addToken.addToLine(token, TokenEnums.tokenCase);
                    i += 3;
                    break;
                }
                case "READ": {
                    Token token = new Token(TokenType.keyword, "READ", tokenLocation);
                    addToken.addToLine(token, TokenEnums.read);
                    i += 3;
                    break;
                }
            } // end of fourth switch


            if (i+4 >= this.line.length()) { continue; }


            // start of fifth switch
            char c4 = this.line.charAt(i+4);
            sb0.append(c4);
            switch(sb0.toString()) {
                case "ARRAY": {
                    Token token = new Token(TokenType.keyword, "ARRAY", tokenLocation);
                    addToken.addToLine(token, TokenEnums.typeArray);
                    i += 4;
                    break;
                }
                case "ENDIF": {
                    Token token = new Token(TokenType.keyword, "ENDIF", tokenLocation);
                    addToken.addToLine(token, TokenEnums.endIf);
                    i += 4;
                    break;
                }
                case "UNTIL": {
                    Token token = new Token(TokenType.keyword, "UNTIL", tokenLocation);
                    addToken.addToLine(token, TokenEnums.until);
                    i += 4;
                    break;
                }
                case "WHILE": {
                    Token token = new Token(TokenType.keyword, "WHILE", tokenLocation);
                    addToken.addToLine(token, TokenEnums.tokenWhile);
                    i += 4;
                    break;
                }
                case "INPUT": {
                    Token token = new Token(TokenType.keyword, "INPUT", tokenLocation);
                    addToken.addToLine(token, TokenEnums.input);
                    i += 4;
                    break;
                }
                case "ROUND": {
                    Token token = new Token(TokenType.keyword, "ROUND", tokenLocation);
                    addToken.addToLine(token, TokenEnums.round);
                    i += 4;
                    break;
                }
                case "UCASE": {
                    Token token = new Token(TokenType.keyword, "UCASE", tokenLocation);
                    addToken.addToLine(token, TokenEnums.uCase);
                    i += 4;
                    break;
                }
                case "LCASE": {
                    Token token = new Token(TokenType.keyword, "LCASE", tokenLocation);
                    addToken.addToLine(token, TokenEnums.lCase);
                    i += 4;
                    break;
                }
                case "WRITE": {
                    Token token = new Token(TokenType.keyword, "WRITE", tokenLocation);
                    addToken.addToLine(token, TokenEnums.write);
                    i += 4;
                    break;
                }
            }// end of fifth switch


            if (i+5 >= this.line.length()) { continue; }


            // Start of sixth switch
            char c5 = this.line.charAt(i+5);
            sb0.append(c5);
            switch(sb0.toString()) {
                case "STRING": {
                    Token token = new Token(TokenType.keyword, "STRING", tokenLocation);
                    addToken.addToLine(token, TokenEnums.typeString);
                    i += 5;
                    break;
                }
                case "RETURN": {
                    Token token = new Token(TokenType.keyword, "RETURN", tokenLocation);
                    addToken.addToLine(token, TokenEnums.tokenReturn);
                    i += 5;
                    break;
                }
                case "REPEAT": {
                    Token token = new Token(TokenType.keyword, "REPEAT", tokenLocation);
                    addToken.addToLine(token, TokenEnums.repeat);
                    i += 5;
                    break;
                }
                case "OUTPUT": {
                    Token token = new Token(TokenType.keyword, "OUTPUT", tokenLocation);
                    addToken.addToLine(token, TokenEnums.output);
                    i += 5;
                    break;
                }
                case "RANDOM": {
                    Token token = new Token(TokenType.keyword, "RANDOM", tokenLocation);
                    addToken.addToLine(token, TokenEnums.random);
                    i += 5;
                    break;
                }
                case "LENGTH": {
                    Token token = new Token(TokenType.keyword, "LENGTH", tokenLocation);
                    addToken.addToLine(token, TokenEnums.length);
                    i += 5;
                    break;
                }
            } // end of six switch


            if (i+6 >= this.line.length()) { continue; }


            // start of seventh switch
            char c6 = this.line.charAt(i+6);
            sb0.append(c6);
            switch(sb0.toString()) {
                case "INTEGER": {
                    Token token = new Token(TokenType.keyword, "INTEGER", tokenLocation);
                    addToken.addToLine(token, TokenEnums.typeInteger);
                    i += 6;
                    break;
                }
                case "BOOLEAN": {
                    Token token = new Token(TokenType.keyword, "BOOLEAN", tokenLocation);
                    addToken.addToLine(token, TokenEnums.typeBoolean);
                    i += 6;
                    break;
                }
                case "RETURNS": {
                    Token token = new Token(TokenType.keyword, "RETURNS", tokenLocation);
                    addToken.addToLine(token, TokenEnums.returns);
                    i += 6;
                    break;
                }
                case "DECLARE": {
                    Token token = new Token(TokenType.keyword, "DECLARE", tokenLocation);
                    addToken.addToLine(token, TokenEnums.declare);
                    i += 6;
                    break;
                }
                case "ENDCASE": {
                    Token token = new Token(TokenType.keyword, "ENDCASE", tokenLocation);
                    addToken.addToLine(token, TokenEnums.endCase);
                    i += 6;
                    break;
                }
            }// end of seventh switch


            if (i+7 >= this.line.length()) { continue; }


            // start of eighth switch
            char c7 = this.line.charAt(i+7);
            sb0.append(c7);
            switch(sb0.toString()) {
                case "FUNCTION": {
                    Token token = new Token(TokenType.keyword, "FUNCTION", tokenLocation);
                    addToken.addToLine(token, TokenEnums.function);
                    i += 7;
                    break;
                }
                case "CONSTANT": {
                    Token token = new Token(TokenType.keyword, "CONSTANT", tokenLocation);
                    addToken.addToLine(token, TokenEnums.constant);
                    i += 7;
                    break;
                }
                case "ENDWHILE": {
                    Token token = new Token(TokenType.keyword, "ENDWHILE", tokenLocation);
                    addToken.addToLine(token, TokenEnums.endWhile);
                    i += 7;
                    break;
                }
                case "OPENFILE": {
                    Token token = new Token(TokenType.keyword, "OPENFILE", tokenLocation);
                    addToken.addToLine(token, TokenEnums.openFile);
                    i += 7;
                    break;
                }
                case "READFILE": {
                    Token token = new Token(TokenType.keyword, "READFILE", tokenLocation);
                    addToken.addToLine(token, TokenEnums.readFile);
                    i += 7;
                    break;
                }
            } // end of the eighth switch


            if (i+8 >= this.line.length()) { continue; }


            // start of ninth switch
            char c8 = this.line.charAt(i+8);
            sb0.append(c8);
            switch(sb0.toString()) {
                case "PROCEDURE": {
                    Token token = new Token(TokenType.keyword, "PROCEDURE", tokenLocation);
                    addToken.addToLine(token, TokenEnums.procedure);
                    i += 8;
                    break;
                }
                case "OTHERWISE": {
                    Token token = new Token(TokenType.keyword, "OTHERWISE", tokenLocation);
                    addToken.addToLine(token, TokenEnums.otherwise);
                    i += 8;
                    break;
                }
                case "SUBSTRING": {
                    Token token = new Token(TokenType.keyword, "SUBSTRING", tokenLocation);
                    addToken.addToLine(token, TokenEnums.subString);
                    i += 8;
                    break;
                }
                case "WRITEFILE": {
                    Token token = new Token(TokenType.keyword, "WRITEFILE", tokenLocation);
                    addToken.addToLine(token, TokenEnums.writeFile);
                    i += 8;
                    break;
                }
                case "CLOSEFILE": {
                    Token token = new Token(TokenType.keyword, "CLOSEFILE", tokenLocation);
                    addToken.addToLine(token, TokenEnums.closeFile);
                    i += 8;
                    break;
                }
            } // end of ninth switch


            if (i+9 >= this.line.length()) { continue; }


            // start of tenth switch
            char c9 = this.line.charAt(i+9);
            sb0.append(c9);
            switch(sb0.toString()) { // we just make switches up until the last one in case we need to add more keywords in the future!

            } // end of tenth swtich


            if (i+10 >= this.line.length()) { continue; }


            // start of eleven switch
            char c10 = this.line.charAt(i+10);
            sb0.append(c10);
            switch(sb0.toString()) {
                case "ENDFUNCTION": {
                    Token token = new Token(TokenType.keyword, "ENDFUNCTION", tokenLocation);
                    addToken.addToLine(token, TokenEnums.endFunction);
                    i += 10;
                    break;
                }
            } // end of eleventh switch


            if (i+11 >= this.line.length()) { continue; }


            // start of twelth and FINAL switch!
            char c11 = this.line.charAt(i+11);
            sb0.append(c11);
            switch(sb0.toString()) {
                case "ENDPROCEDURE": {
                    Token token = new Token(TokenType.keyword, "ENDPROCEDURE", tokenLocation);
                    addToken.addToLine(token, TokenEnums.endProcedure);
                    i += 11;
                    break;
                }
            } // end of twelth switch

        } // end of for loop

        TokenLocation tokenLocation = new TokenLocation(this.lineNumber, this.line.length());
        Token token = new Token(TokenType.control, "", tokenLocation);
        addToken.addToLine(token, TokenEnums.eol);

        return addToken.getTokens();

    } // end of method tokenize

    // artifact from previous method, saved incase needed for future reference

    private int helperCheckForTerm(int index, int addOn, AddToken addToken, StringBuilder sb, TokenLocation tokenLocation) {
        if (this.line.charAt(index + addOn + 1) < 128 && endChar[this.line.charAt(index + addOn + 1)]) {
            Token token = new Token(TokenType.identifier, sb.toString(), tokenLocation);
            addToken.addToLine(token, TokenEnums.identifier);
            return index + addOn;
        }
        return index;
    }

    // WHAT THIS HELPER FUNCTION DOES AT THE END OF EACH SWITCH BASICALLY
    // USE LIKE default: { i = this.helperCheckForTerm(i, _, addToken, sb0, tokenLocation) }
//    default: {
//
//        // check for a terminating symbol or character to recognise an identifier which is a character
//        if ( this.line.charAt(i+3) < 128 && endChar[this.line.charAt(i+3)]) {
//            Token token = new Token(TokenType.identifier, sb0.toString(), tokenLocation);
//            addToken.addToLine(token, TokenEnums.identifier);
//            i += 2;
//            break;
//        }
//    }


}
