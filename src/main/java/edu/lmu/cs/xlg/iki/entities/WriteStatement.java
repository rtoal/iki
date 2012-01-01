package edu.lmu.cs.xlg.iki.entities;

import java.util.List;

/**
 * An Iki write statement.
 */
public class WriteStatement extends Statement {

    private List<Expression> expressions;

    public WriteStatement(List<Expression> expressions) {
        this.expressions = expressions;
    }

    public List<Expression> getExpressions() {
        return expressions;
    }
}
