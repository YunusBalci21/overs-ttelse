package dk.sdu.teaching.compiler.fs24.spl.ast.expr;

import dk.sdu.teaching.compiler.fs24.spl.ast.Expr;
import dk.sdu.teaching.compiler.fs24.spl.ast.ExprVisitor;
import dk.sdu.teaching.compiler.fs24.spl.scan.Token;

public class Variable extends Expr {
	public final Token name;

	public Variable(Token name) {
		this.name = name;
	}

	@Override
	public <T> T accept(ExprVisitor<T> visitor) {
		return visitor.visitVariableExpr(this);
	}

}
