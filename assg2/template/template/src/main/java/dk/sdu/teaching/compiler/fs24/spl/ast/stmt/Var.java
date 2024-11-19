package dk.sdu.teaching.compiler.fs24.spl.ast.stmt;

import dk.sdu.teaching.compiler.fs24.spl.ast.Expr;
import dk.sdu.teaching.compiler.fs24.spl.ast.Stmt;
import dk.sdu.teaching.compiler.fs24.spl.ast.StmtVisitor;
import dk.sdu.teaching.compiler.fs24.spl.scan.Token;

public class Var extends Stmt {
	public final Token name;
	public final Expr initializer;
	
	public Var(Token name, Expr initializer) {
		this.name = name;
		this.initializer = initializer;
	}

	@Override
	public <T> T accept(StmtVisitor<T> visitor) {
		return visitor.visitVarStmt(this);
	}
}
