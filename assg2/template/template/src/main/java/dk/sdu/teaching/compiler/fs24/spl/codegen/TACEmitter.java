package dk.sdu.teaching.compiler.fs24.spl.codegen;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import dk.sdu.teaching.compiler.fs24.spl.ast.*;
import dk.sdu.teaching.compiler.fs24.spl.ast.expr.*;
import dk.sdu.teaching.compiler.fs24.spl.ast.stmt.*;
import dk.sdu.teaching.compiler.fs24.spl.scan.TokenType;

public class TACEmitter implements ExprVisitor<String>, StmtVisitor<Void> {
    private List<String> code = new ArrayList<>();
    private int tempCounter = 1;
    private int labelCounter = 1;

    // Generate a temporary variable
    private String generateTemp() {
        return "_t" + tempCounter++;
    }

    // Generate a label for control flow
    private String generateLabel() {
        return "L" + labelCounter++;
    }

    // Add a TAC instruction to the code list
    private void emit(String instruction) {
        String cleanedInstruction = instruction.replaceAll("[*]t", "_t");
        code.add(cleanedInstruction);
    }

    // Generate TAC code for a list of statements
    public void generateCode(List<Stmt> statements) {
        try {
            try (PrintWriter writer = new PrintWriter(new FileWriter("output.tac"))) {
                tempCounter = 1;
                labelCounter = 1;
                code.clear();

                for (Stmt stmt : statements) {
                    stmt.accept(this);
                }

                // Write all lines except the last with newlines
                for (int i = 0; i < code.size() - 1; i++) {
                    writer.println(code.get(i));
                }

                // Write the last line without a trailing newline
                if (!code.isEmpty()) {
                    writer.print(code.get(code.size() - 1));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String visitAssignExpr(Assign expr) {
        String value = expr.value.accept(this);
        emit(expr.name.lexeme + " = " + value);
        return expr.name.lexeme;
    }

    @Override
    public String visitBinaryExpr(Binary expr) {
        String left = expr.left.accept(this);
        String right = expr.right.accept(this);
        String temp = generateTemp();

        String operator = switch (expr.operator.type) {
            case PLUS -> "+";
            case MINUS -> "-";
            case MULT -> "*";
            case DIV -> "/";
            case LESS -> "<";
            case GREATER -> ">";
            case LESS_EQUAL -> "<=";
            case GREATER_EQUAL -> ">=";
            case EQUAL_EQUAL -> "==";
            case NOT_EQUAL -> "!=";
            default -> throw new RuntimeException("Unknown operator: " + expr.operator.type);
        };

        String instruction = String.format("%s = %s %s %s", temp, left, operator, right);
        emit(instruction);
        return temp;
    }

    @Override
    public String visitLiteralExpr(Literal expr) {
        String temp = generateTemp();
        if (expr.value == null) {
            emit(temp + " = null");
        } else if (expr.value instanceof String) {
            emit(temp + " = \"" + expr.value + "\"");
        } else if (expr.value instanceof Number) {
            double value = ((Number) expr.value).doubleValue();
            if (value == Math.floor(value)) {
                emit(temp + " = " + (int) value);
            } else {
                emit(temp + " = " + value);
            }
        }
        return temp;
    }

    @Override
    public String visitLogicalExpr(Logical expr) {
        String left = expr.left.accept(this);
        String right = expr.right.accept(this);
        String temp = generateTemp();

        String operator = expr.operator.type == TokenType.AND ? "&&" : "||";
        String instruction = String.format("%s = %s %s %s", temp, left, operator, right);
        emit(instruction);
        return temp;
    }

    @Override
    public String visitUnaryExpr(Unary expr) {
        String operand = expr.right.accept(this);
        String temp = generateTemp();

        String operator = switch (expr.operator.type) {
            case MINUS -> "-";
            case NOT -> "!";
            default -> throw new RuntimeException("Unknown operator: " + expr.operator.type);
        };

        emit(temp + " = " + operator + operand);
        return temp;
    }

    @Override
    public String visitVariableExpr(Variable expr) {
        return expr.name.lexeme;
    }

    @Override
    public Void visitBlockStmt(Block stmt) {
        for (Stmt s : stmt.statements) {
            s.accept(this);
        }
        return null;
    }

    @Override
    public Void visitExpressionStmt(Expression stmt) {
        stmt.expression.accept(this);
        return null;
    }

    @Override
    public Void visitIfStmt(If stmt) {
        String condition = stmt.condition.accept(this);
        String elseLabel = generateLabel();
        String endLabel = generateLabel();

        emit("IfZ " + condition + " goto " + elseLabel);
        stmt.thenBranch.accept(this);
        emit("goto " + endLabel);
        emit(elseLabel + ":");
        if (stmt.elseBranch != null) {
            stmt.elseBranch.accept(this);
        }
        emit(endLabel + ":");
        return null;
    }

    @Override
    public Void visitPrintStmt(Print stmt) {
        // Optional: implement if debugging TAC output with print statements
        return null;
    }

    @Override
    public Void visitVarStmt(Var stmt) {
        if (stmt.initializer != null) {
            String value = stmt.initializer.accept(this);
            emit(stmt.name.lexeme + " = " + value);
        }
        return null;
    }

    @Override
    public Void visitWhileStmt(While stmt) {
        String startLabel = generateLabel();
        String endLabel = generateLabel();

        emit(startLabel + ":");
        String condition = stmt.condition.accept(this);
        emit("IfZ " + condition + " goto " + endLabel);
        stmt.body.accept(this);
        emit("goto " + startLabel);
        emit(endLabel + ":");
        return null;
    }
}
