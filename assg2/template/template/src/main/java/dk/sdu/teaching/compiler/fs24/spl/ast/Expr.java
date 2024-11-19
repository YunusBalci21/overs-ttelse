package dk.sdu.teaching.compiler.fs24.spl.ast;

public abstract class Expr {
	public abstract <T> T accept(ExprVisitor<T> visitor);
}
