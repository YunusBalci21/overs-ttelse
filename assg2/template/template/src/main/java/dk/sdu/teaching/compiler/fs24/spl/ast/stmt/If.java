package dk.sdu.teaching.compiler.fs24.spl.ast.stmt;

import dk.sdu.teaching.compiler.fs24.spl.ast.Expr;
import dk.sdu.teaching.compiler.fs24.spl.ast.Stmt;
import dk.sdu.teaching.compiler.fs24.spl.ast.StmtVisitor;

public class If extends Stmt {
	public final Expr condition;
	public final Stmt thenBranch;
	public final Stmt elseBranch;

	public If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
		this.condition = condition;
		this.thenBranch = thenBranch;
		this.elseBranch = elseBranch;
	}

	@Override
	public <T> T accept(StmtVisitor<T> visitor) {
		return visitor.visitIfStmt(this);
	}
}
