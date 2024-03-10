package lexer;

public enum TokenType {
    IDENTIFIER, // [a-zA-Z][a-zA-Z0-9_]*
    VALUE_NUMBER, // [0-9]+
    VALUE_STRING, //'ASCII Char'
    VALUE_BOOLEAN,
    END_OF_FILE, // dulo na ng file
    UNRECOGNIZED_TOKEN, // hindi existing character/token 
    
    // Keywords
    	// Reserved
    	INITIALIZER,
    	KEY_INPUT, // input
    	KEY_OUTPUT, // output
    	KEY_PUBLIC, //public
    	KEY_PRIVATE, //private
    
    	// Datatypes
    	TYPE_NUM, // int, float, double
    	TYPE_STR, // char, String
    	TYPE_BOOL, // boolean
    	TYPE_FLAT, // constant
    	
    	// Conditions
    	COND_IF, // if
        COND_ELSE, // else
        COND_ELSIF, // elseif
        
        //Loops
        LOOP_WHILE,
    	
    // Logical Operators
    LOG_AND, // &&
    LOG_OR, // ||
    LOG_EQ, // ==
    LOG_NEQ, // !=
    LOG_LESS_THAN, // <
    LOG_GREATER_THAN, // >
    LOG_LESS_THAN_EQ, // <=
    LOG_GREATER_THAN_EQ, // >=
    
    // Arithmetic Operators
    MATH_ADD, // +
    MATH_SUB, // -
    MATH_MUL, // *
    MATH_DIV, // /
    MATH_EXPONENT, // ^
    
    // Punctuation
    LPAREN, // (
    RPAREN, // )
    LBRACKET, //{
    RBRACKET, //}
    COLON, //:
    SEMICOLON, //;
    COMMA, // ,
    ASSIGN, // =
    NOT, // !

    // Errors
    STATEMENT,
    EXPRESSION,
    OPERATOR,
    TYPE
}