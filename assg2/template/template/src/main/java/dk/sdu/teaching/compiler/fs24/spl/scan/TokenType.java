package dk.sdu.teaching.compiler.fs24.spl.scan;

public enum TokenType {

    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE, MINUS, PLUS, SEMICOLON, DIV, MULT,

	// One or two character tokens
	NOT, NOT_EQUAL, EQUAL, EQUAL_EQUAL, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL,

	// Literals
	IDENTIFIER, STRING, NUMBER,

	// Keywords
	AND, ELSE, FALSE, IF, OR, PRINT, TRUE, VAR, WHILE,

	// End-of-file
	EOF
}
