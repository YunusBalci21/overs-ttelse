package dk.sdu.teaching.compiler.fs24.spl.ast.expr;

import dk.sdu.teaching.compiler.fs24.spl.ast.Expr;
import dk.sdu.teaching.compiler.fs24.spl.ast.ExprVisitor;

public class Literal extends Expr {
	public final Object value;

	public Literal(Object value) {
		this.value = value;
	}

	@Override
	public <T> T accept(ExprVisitor<T> visitor) {
		return visitor.visitLiteralExpr(this);
	}
}
