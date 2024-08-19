package main.ast.node.statement;

import main.ast.node.expression.Expression;
import main.visitor.IVisitor;
import main.ast.node.expression.operators.*;;

public class AssignStmt extends Statement {
    private Expression lValue;
    private Expression rValue;
    private BinaryOperator OP;

    public AssignStmt(Expression lValue, Expression rValue) {
        this.lValue = lValue;
        this.rValue = rValue;
    }

    public Expression getLValue() {
        return lValue;
    }

    public void setLValue(Expression lValue) {
        this.lValue = lValue;
    }

    public Expression getRValue() {
        return rValue;
    }

    public void setRValue(Expression rValue) {
        this.rValue = rValue;
        // System.out.println("RVALUE SET");
    }

    public BinaryOperator getOP() {
        return this.OP;
    }

    public void setOP(BinaryOperator op) {
        System.out.println("hello bitches im op");
        this.OP = op;
    }

    @Override
    public String toString() {
        return "AssignStmt";
    }

    @Override
    public <T> T accept(IVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
