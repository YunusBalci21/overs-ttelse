package dk.sdu.teaching.compiler.fs24.spl.ast.expr;

import dk.sdu.teaching.compiler.fs24.spl.ast.Expr;
import dk.sdu.teaching.compiler.fs24.spl.ast.ExprVisitor;
import dk.sdu.teaching.compiler.fs24.spl.scan.Token;

public class Assign extends Expr {
	public final Token name;
	public final Expr value;

	public Assign(Token name, Expr value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public <T> T accept(ExprVisitor<T> visitor) {
		return visitor.visitAssignExpr(this);
	}
}
