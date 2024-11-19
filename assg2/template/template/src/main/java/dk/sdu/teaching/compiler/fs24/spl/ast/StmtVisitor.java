package dk.sdu.teaching.compiler.fs24.spl.ast;

import dk.sdu.teaching.compiler.fs24.spl.ast.stmt.*;


public interface StmtVisitor<T> {
	T visitBlockStmt(Block stmt);
	T visitExpressionStmt(Expression stmt);
	T visitIfStmt(If stmt);
	T visitPrintStmt(Print stmt);
	T visitVarStmt(Var stmt);
	T visitWhileStmt(While stmt);
}
