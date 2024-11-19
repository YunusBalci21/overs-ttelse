package dk.sdu.teaching.compiler.fs24.spl.ast.stmt;

import java.util.List;

import dk.sdu.teaching.compiler.fs24.spl.ast.Stmt;
import dk.sdu.teaching.compiler.fs24.spl.ast.StmtVisitor;

public class Block extends Stmt {
	public final List<Stmt> statements;
	
	public Block(List<Stmt> statements) {
		this.statements = statements;
	}

	@Override
	public <T> T accept(StmtVisitor<T> visitor) {
		return visitor.visitBlockStmt(this);
	}
}
