package dk.sdu.teaching.compiler.fs24.spl.parse;

import java.util.LinkedList;
import java.util.List;

import dk.sdu.teaching.compiler.fs24.spl.ast.Expr;
import dk.sdu.teaching.compiler.fs24.spl.ast.Stmt;
import dk.sdu.teaching.compiler.fs24.spl.ast.expr.Assign;
import dk.sdu.teaching.compiler.fs24.spl.ast.expr.Binary;
import dk.sdu.teaching.compiler.fs24.spl.ast.expr.Literal;
import dk.sdu.teaching.compiler.fs24.spl.ast.expr.Logical;
import dk.sdu.teaching.compiler.fs24.spl.ast.expr.Unary;
import dk.sdu.teaching.compiler.fs24.spl.ast.expr.Variable;
import dk.sdu.teaching.compiler.fs24.spl.ast.stmt.Block;
import dk.sdu.teaching.compiler.fs24.spl.ast.stmt.Expression;
import dk.sdu.teaching.compiler.fs24.spl.ast.stmt.If;
import dk.sdu.teaching.compiler.fs24.spl.ast.stmt.Print;
import dk.sdu.teaching.compiler.fs24.spl.ast.stmt.Var;
import dk.sdu.teaching.compiler.fs24.spl.ast.stmt.While;
import dk.sdu.teaching.compiler.fs24.spl.scan.Token;
import dk.sdu.teaching.compiler.fs24.spl.scan.TokenType;



public class Parser {

	private List<Token> tokens;
	private int current = 0;

	public Parser(List<Token> tokens) {
		this.tokens = tokens;
	}

	public List<Stmt> parse() {
		List<Stmt> statements = new LinkedList<>();
		while(!isAtEnd()) {
			statements.add(declaration());
		}
		return statements;
	}

	private Stmt declaration() {
		if (match(TokenType.VAR)) 
			return varDeclaration();
		return statement();
	}

	private Expr expression() {
		return assignment();
	}

	private Stmt statement() {
		if (match(TokenType.IF))
			return ifStatement();
		if (match(TokenType.PRINT))
			return printStatement();
		if (match(TokenType.WHILE))
			return whileStatement();
		if (match(TokenType.LEFT_BRACE))
			return new Block(block());
		return expressionStatement();
	}

	private Stmt ifStatement() {
		consume(TokenType.LEFT_PAREN, "Expect '(' after 'if'.");
		Expr condition = expression();
		consume(TokenType.RIGHT_PAREN, "Expect ')' after if condition."); 

		Stmt thenBranch = statement();
		Stmt elseBranch = null;
		if (match(TokenType.ELSE)) 
			elseBranch = statement();		

		return new If(condition, thenBranch, elseBranch);
	}

	private Stmt printStatement() {
		Expr value = expression();
		consume(TokenType.SEMICOLON, "Expect ';' after value.");
		return new Print(value);
	}

	private Stmt varDeclaration() {
		Token name = consume(TokenType.IDENTIFIER, "Expect variable name.");

		Expr initializer = null;
		if (match(TokenType.EQUAL)) 
			initializer = expression();

		consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.");
		return new Var(name, initializer);
	}

	private Stmt whileStatement() {
		consume(TokenType.LEFT_PAREN, "Expect '(' after 'while'.");
		Expr condition = expression();
		consume(TokenType.RIGHT_PAREN, "Expect ')' after condition.");
		Stmt body = statement();

		return new While(condition, body);
	}

	private Stmt expressionStatement() {
		Expr expr = expression();
		consume(TokenType.SEMICOLON, "Expect ';' after expression.");
		return new Expression(expr);
	}

	private List<Stmt> block() {
		List<Stmt> statements = new LinkedList<>();

		while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
			statements.add(declaration());
		}

		consume(TokenType.RIGHT_BRACE, "Expect '}' after block.");
		return statements;
	}

	private Expr assignment() {
		Expr expr = or();
		
		if (match(TokenType.EQUAL)) {
			Expr value = assignment();

			if (expr instanceof Variable) {
				Token name = ((Variable) expr).name;
				return new Assign(name, value);
			}
		}

		return expr;
	}

	private Expr or() {
		Expr expr = and();

		while (match(TokenType.OR)) {
			Token operator = previous();
			Expr right = and();
			expr = new Logical(expr, operator, right);
		}

		return expr;
	}

	private Expr and() {
		Expr expr = equality();

		while (match(TokenType.AND)) {
			Token operator = previous();
			Expr right = equality();
			expr = new Logical(expr, operator, right);
		}

		return expr;
	}

	private Expr equality() {
		Expr expr = comparison();

		while (match(TokenType.NOT_EQUAL, TokenType.EQUAL_EQUAL)) {
			Token operator = previous();
			Expr right = comparison();
			expr = new Binary(expr, operator, right);
		}

		return expr;
	}

	private Expr comparison() {
		Expr expr = term();

		while (match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
			Token operator = previous();
			Expr right = term();
			expr = new Binary(expr, operator, right);
		}

		return expr;
	}

	private Expr term() {
		Expr expr = factor();

		while (match(TokenType.MINUS, TokenType.PLUS)) {
			Token operator = previous();
			Expr right = factor();
			expr = new Binary(expr, operator, right);
		}

		return expr;
	}

	private Expr factor() {
		Expr expr = unary();

		while (match(TokenType.DIV, TokenType.MULT)) {
			Token operator = previous();
			Expr right = unary();
			expr = new Binary(expr, operator, right);
		}

		return expr;
	}

	private Expr unary() {
		if (match(TokenType.NOT, TokenType.MINUS)) {
			Token operator = previous();
			Expr right = unary();
			return new Unary(operator, right);
		}

		return primary();
	}

	private Expr primary() {
		if (match(TokenType.FALSE))
			return new Literal(false);
		if (match(TokenType.TRUE))
			return new Literal(true);

		if (match(TokenType.NUMBER, TokenType.STRING)) {
			return new Literal(previous().literal);
		}

		if (match(TokenType.IDENTIFIER)) {
			return new Variable(previous());
		}

		return null;
	}

	private boolean match(TokenType... types) {
		for (TokenType type : types) {
			if (check(type)) {
				advance();
				return true;
			}
		}
		return false;
	}

	private Token consume(TokenType type, String message) {
		if (check(type))
			return advance();
		return null;
	}

	private boolean check(TokenType type) {
		if (isAtEnd())
			return false;
		return peek().type == type;
	}

	private Token advance() {
		if (!isAtEnd())
			current++;
		return previous();
	}

	private boolean isAtEnd() {
		return peek().type == TokenType.EOF;
	}

	private Token peek() {
		return tokens.get(current);
	}

	private Token previous() {
		return tokens.get(current - 1);
	}
}