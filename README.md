# Pseudocode-Interpreter
## Made by Zeftarium!
<br>
<br>
This is an interpreter for a language that is based off the CAIE syllabus syntax for their pseudocode.

### Lexer

The lexer is split into many small parts with the biggest part being the Line Tokenizer, 
holding the main logic for all the tokenizing processes and logic. In this section we will go through what
each thing does and how does it contribute to the lexing process of the interpreter.

#### MainLexer.java

This java class simply helps intertwine all the features of the other classes into one class using composition.
What MainLexer does is that it scans line by line and creates a new instance of LineTokenizer which in the creation
of LineTokenizer it feeds a new line into it and at the end it gets a full table of tokens as we add them all up 
in an Arraylist.

```
Scanner scanner = new Scanner(this.file);

        int i = 0;
        while (scanner.hasNextLine()) {
            LineTokenizer lineTokenizer = new LineTokenizer(scanner.nextLine(), i);
            this.lexedLine.addAll( lineTokenizer.tokenize() ); // adding it to the lexedLine the line that we lexed!
            i++;
        }
```
As seen in this code as shown above ^ this simply creates a scanner instance and has that scanner iterate line by
line and tokenize each line then adding it to an Arraylist where it stores the token objects.