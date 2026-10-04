# Pseudocode-Interpreter
## Made by Zeftarium!
This is an interpreter for a language that is based off the CAIE syllabus syntax for their pseudocode.

## Disclaimer

This is an independent educational project implementing a language inspired
by Cambridge IGCSE pseudocode. It is not affiliated with, endorsed by,
sponsored by, or approved by Cambridge International Education (CAIE).
References to Cambridge and its syllabus identify the intended syntax
reference only.

This interpreter is not an official syntax validator, marking tool, or
assessment authority. Its behaviour may differ from the syllabus or contain
errors. Acceptance or rejection of a program does not determine whether an
exam answer is correct, would receive marks, or meets a grade threshold.
Consult the relevant official syllabus, examination materials, and mark
schemes for assessment guidance.

The software is provided “as is”, without warranties of any kind, to the
extent permitted by applicable law. No guarantee is made regarding its
accuracy, reliability, suitability for any purpose, or educational outcomes.
You use the software and rely on its results at your own risk.

To the extent permitted by applicable law, and except as otherwise agreed
in writing, the authors and contributors disclaim liability for loss or
damage arising from use of, inability to use, or reliance on the software.

See [LICENSE](LICENSE), particularly sections 15–17, for the applicable
warranty disclaimer and limitation of liability. This notice summarises
those provisions and does not replace or modify the licence.

### Lexer

The lexer is split into many small parts with the biggest part being the Line Tokenizer, 
holding the main logic for all the tokenizing processes and logic. In this section we will go through what
each thing does and how does it contribute to the lexing process of the interpreter.

#### MainLexer.java

This java class simply helps intertwine all the features of the other classes into one class using composition.
What MainLexer does is that it scans line by line and creates a new instance of LineTokenizer which in the creation
of LineTokenizer it feeds a new line into it and at the end it gets a full table of tokens as we add them all up 
in an Arraylist.

```java 
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
<br>
<br>
Another thing that the MainLexer.java is responsible for is to add the end of file token to denote when it is the end 
of the file which stops the parser from parsing later. It is implemented like so;
```java 
AddToken addToken = new AddToken();
TokenLocation tokenLocation = new TokenLocation(i,0);
Token token = new Token(TokenType.control, "EndOfFile", tokenLocation);
```

#### LineTokenizer.java

This java class does most of the work in the lexer package as it is what converts and adds the tokens for each
line. It basically just searches for any single character tokens then looks for a literal or identifier then
matches any other tokens it finds to a token and adds that to the list. It does this for each line which is then
all summed up and added to one big list later on in MainLexer.java.

Each unique non identifier token is created by using a switch statement in which it matches the correct keywords
to the correct tokens. Here is an example of what this would look like;

```java
switch(stringA) { // checks for two character tokens
    case "<-": {
        Token token = new Token(TokenType.operator, "<-", tokenLocation);
        addToken.addToLine(token, TokenEnums.assignment);
        i++; // we skip the next iteration as thats basically the same token
        break;
    }
```

We essentially check for each keyword in the language to generate the appropriate token for it and add it to the list
for that line which we return back later on with the addition of an end of line token. 

### Parser

#### MainParser.java

This is the main part of the parser which handles parsing the tokens into an AST that the evaluator can evaluate.
It is a top-down recursive descent parser with a one token lookahead. This means parsing starts at the program
and then breaks down into to smaller pieces and with a one token lookahead it looks at the next token to make a
decision. It represents each node with a class that is implemented from an AST package which holds the interfaces
for all the other node classes in the parser package. An example of how a node class would be structured would be
like so; 

```java 
public class Assignment implements ast.Assignment {

    private final Assignable target;
    private final Expression value;

    public Assignment(Assignable target, Expression value) {
        this.target = target;
        this.value = value;
    }

    @Override
    public NodeType kind() {
        return NodeType.assignment;
    }

    @Override
    public Assignable target() {
        return this.target;
    }

    @Override
    public Expression value() {
        return this.value;
    }

}
```

Essentially we implement an interface from the AST, matching the class. Each class would usually have a kind which 
returns its NodeType.