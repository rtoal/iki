package edu.lmu.cs.xlg.iki.entities;

/**
 * An Iki while statement.
 */
public class WhileStatement extends Statement {

    private Expression condition;
    private Block body;

    public WhileStatement(Expression condition, Block body) {
        this.condition = condition;
        this.body = body;
    }

    public Expression getCondition() {
        return condition;
    }

    public Block getBody() {
        return body;
    }
}
