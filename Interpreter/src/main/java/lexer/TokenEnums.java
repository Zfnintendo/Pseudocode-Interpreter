package lexer;

// NOTE: ALL KEYWORDS ARE FULLY CAPITALISED IN REGULAR SYNTAX

public enum TokenEnums {

    // default token incase needed
    tokenDefault,

    // types | INTEGER, REAL, CHAR, STRING, BOOLEAN
    typeInteger,
    typeReal,
    typeChar,
    typeString,
    typeBoolean,
    typeArray,

    // literals
    literalInteger,
    literalReal,
    literalChar,
    literalString,
    literalBoolean,
    literalArray,

    // Identifier | like func _x_ or var _y_ or _print_
    identifier,

    // procedures and functions
    procedure,
    endProcedure,
    // function
    function,
    endFunction,
    tokenReturn, // like return x+x
    returns, //  like function(): int
    call,

    // symbols & operators | + - = * ( ) < > <= >= <- / : "
    assignment, // <-
    colon, // :
    // arithmatic operators
    addition, // +
    subtraction, // -
    multiplication, // *
    division, // /
    exponent, // ^
    equals, // =
    // mod and div
    div, // integer division "DIV()"
    mod, // modulo of something like % "MOD()"
    // Logical Operators
    and, // AND
    or, // OR
    not, // NOT
    // those brackets or parenthesis
    leftParen, // (
    rightParen, // )
    leftBracket, // [
    rightBracket, // ]
    // comparison operators
    lessThan, // <
    greaterThan, // >
    lessEqualTo, // <=
    greaterEqualTo, // >=
    notEqualTo, // <>
    // strings and concatenators
    doubleQuote, // "
    singleQuote, // '
    comma, // ,

    // statements
    declare,
    of, // OF
    constant,
    // if statements
    tokenIf, // if
    then,
    tokenElse,
    endIf,
    // repeat until loop
    repeat,
    until,
    // while loop
    tokenWhile,
    endWhile,
    //  for loop
    tokenFor, // for
    next,
    // case
    tokenCase,
    otherwise,
    endCase,

    // built-in methods functions
    input,
    output,
    // round and random
    round,
    random,
    // string handling
    length,
    uCase,
    lCase,
    subString,

    // file handling
    openFile,
    readFile,
    writeFile,
    closeFile,

    read,
    write,

    // other tokens
    eol, // end of line token
    eof, // end of file token
}
