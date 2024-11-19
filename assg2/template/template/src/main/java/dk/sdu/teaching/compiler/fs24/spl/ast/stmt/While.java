package dk.sdu.teaching.compiler.fs24.spl.ast.stmt;

import dk.sdu.teaching.compiler.fs24.spl.ast.Expr;
import dk.sdu.teaching.compiler.fs24.spl.ast.Stmt;
import dk.sdu.teaching.compiler.fs24.spl.ast.StmtVisitor;

public class While extends Stmt {
	public final Expr condition;
	public final Stmt body;
	
	public While(Expr condition, Stmt body) {
		this.condition = condition;
		this.body = body;
	}

	@Override
	public <T> T accept(StmtVisitor<T> visitor) {
		return visitor.visitWhileStmt(this);
	}
}
