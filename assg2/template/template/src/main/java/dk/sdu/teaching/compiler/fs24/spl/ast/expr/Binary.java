package dk.sdu.teaching.compiler.fs24.spl.ast.expr;

import dk.sdu.teaching.compiler.fs24.spl.ast.Expr;
import dk.sdu.teaching.compiler.fs24.spl.ast.ExprVisitor;
import dk.sdu.teaching.compiler.fs24.spl.scan.Token;

public class Binary extends Expr {
	public final Expr left;
	public final Token operator;
	public final Expr right;

	public Binary(Expr left, Token operator, Expr right) {
		this.left = left;
		this.operator = operator;
		this.right = right;
	}

	@Override
	public <T> T accept(ExprVisitor<T> visitor) {
		return visitor.visitBinaryExpr(this);
	}

}
