package dk.sdu.teaching.compiler.fs24.spl.scan;

import static dk.sdu.teaching.compiler.fs24.spl.scan.TokenType.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {
    // Keyword-map
    private static final Map<String, TokenType> keywords;
    private static final Map<Character, TokenType> singleCharacters;


    
    static {
        keywords = new HashMap<>();
        keywords.put("var", VAR);
        keywords.put("false", FALSE);
        keywords.put("true", TRUE);
        keywords.put("if", IF);
        keywords.put("else", ELSE);
        keywords.put("or", OR);
        keywords.put("and", AND);
        keywords.put("print", PRINT);
        keywords.put("while", WHILE);

        singleCharacters = new HashMap<>();
        singleCharacters.put('(', LEFT_PAREN);
        singleCharacters.put(')', RIGHT_PAREN);
        singleCharacters.put('{', LEFT_BRACE);
        singleCharacters.put('}', RIGHT_BRACE);
        singleCharacters.put('-', MINUS);
        singleCharacters.put('+', PLUS);
        singleCharacters.put(';', SEMICOLON);
        singleCharacters.put('*', MULT);
    }

    public static Map<String, TokenType> getKeywords() {
        return keywords;
    }

    private Map<Integer,String> tokenErrors = new HashMap<>();
    public Map<Integer, String> getTokenErrors() {
        return tokenErrors;
    }

    // In- and output
    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    // Scanning state
    private int start = 0;
    private int current = 0;
    private int line = 1;

    public Scanner(String source) {
        this.source = source;
    }

    // Scan tokens
    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    // Scan token
    private void scanToken() {
        char c = advance();
        TokenType t = singleCharacters.get(c);
        if (t != null)
            addToken(t);
        else {
            switch (c) {
                 // two-char-tokens
                case '!':
                    addToken(match('=') ? NOT_EQUAL : NOT);
                    break;
                case '=':
                    addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                    break;
                case '<':
                    addToken(match('=') ? LESS_EQUAL : LESS);
                    break;
                case '>':
                    addToken(match('=') ? GREATER_EQUAL : GREATER);
                    break;
                case '/':
                    if (match('/')) {
                        // A comment goes until the end of the line.
                        while (peek() != '\n' && !isAtEnd())
                            advance();
                    } else {
                        addToken(DIV);
                    }
                    break;
                // whitespace
                case ' ':
                case '\r':
                case '\t':
                    // Ignore whitespace.
                    break;
                case '\n':
                    line++;
                    break;
                case '"':
                    string();
                    break;

                default:
                    if (isDigit(c)) {
                        number();
                    } else if (isAlpha(c)) {
                        identifier();
                    } else {
                        error(line, "Unexpected character: " + c);
                    }
                    break;
            }
        }
    }

    private void identifier() {
        char peeked = peek();
        while (isAlphaNumeric(peeked) || peeked == '_') {
            advance();
            peeked = peek();
        }

        // Prefer keyword over identifier
        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) {
            type = IDENTIFIER;
        }
        addToken(type);
    }

    private void number() {
        while (isDigit(peek())) {
            advance();
        }

        // Look for a fractional part.
        if (peek() == '.') {
            // Consume the "."
            advance();

            while (isDigit(peek())) {
                advance();
            }
        }

        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') {
                line++;
            }
            advance();
        }

        if (isAtEnd()) {
            error(line, "Unterminated string.");
            return;
        }

        // The closing "
        advance();

        // Cut the surrounding quotes off:
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    private char advance() {
        return source.charAt(current++);
    }

    private boolean match(char expected) {
        if (isAtEnd())
            return false;
        if (source.charAt(current) != expected)
            return false;

        current++;
        return true;
    }

    private char peek() {
        if (isAtEnd())
            return '\0';
        return source.charAt(current);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
    
    private void error(int line, String message) {
		tokenErrors.put(line, message);
	}
}
