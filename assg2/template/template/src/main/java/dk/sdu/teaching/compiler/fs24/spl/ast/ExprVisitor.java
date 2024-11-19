package dk.sdu.teaching.compiler.fs24.spl.ast;

import dk.sdu.teaching.compiler.fs24.spl.ast.expr.*;

public interface ExprVisitor<T> {
	T visitAssignExpr(Assign expr);
	T visitBinaryExpr(Binary expr);
	T visitLiteralExpr(Literal expr);
	T visitLogicalExpr(Logical expr);
	T visitUnaryExpr(Unary expr);
	T visitVariableExpr(Variable expr);

}
