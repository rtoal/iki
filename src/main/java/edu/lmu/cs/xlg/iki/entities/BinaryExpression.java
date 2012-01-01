package edu.lmu.cs.xlg.iki.entities;

import edu.lmu.cs.xlg.util.Log;

/**
 * An Iki binary expression.
 */
public class BinaryExpression extends Expression {

    public static enum Operator {
        PLUS("+"), MINUS("-"), TIMES("*"), DIVIDE("/");

        private String text;

        private Operator(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    private Operator operator;
    private Expression left;
    private Expression right;

    public BinaryExpression(Operator op, Expression x, Expression y) {
        this.operator = op;
        this.left = x;
        this.right = y;
    }

    public Operator getOperator() {
        return operator;
    }

    public Expression getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    }

    @Override
    public void analyze(SymbolTable table, Log log) {
        left.analyze(table, log);
        right.analyze(table, log);
    }
}
