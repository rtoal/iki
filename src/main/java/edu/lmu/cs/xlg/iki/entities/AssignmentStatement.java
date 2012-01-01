package edu.lmu.cs.xlg.iki.entities;

/**
 * An Iki assignment statement.
 */
public class AssignmentStatement extends Statement {

    private VariableReference variableReference;
    private Expression expression;

    public AssignmentStatement(VariableReference v, Expression e) {
        this.variableReference = v;
        this.expression = e;
    }

    public VariableReference getVariableReference() {
        return variableReference;
    }

    public Expression getExpression() {
        return expression;
    }
}
