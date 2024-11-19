package dk.sdu.teaching.compiler.fs24.spl.ast.stmt;

import dk.sdu.teaching.compiler.fs24.spl.ast.Expr;
import dk.sdu.teaching.compiler.fs24.spl.ast.Stmt;
import dk.sdu.teaching.compiler.fs24.spl.ast.StmtVisitor;

public class Print extends Stmt {
	public final Expr expression;
	
	public Print(Expr expression) {
		this.expression = expression;
	}

	@Override
	public <T> T accept(StmtVisitor<T> visitor) {
		return visitor.visitPrintStmt(this);
	}
}
