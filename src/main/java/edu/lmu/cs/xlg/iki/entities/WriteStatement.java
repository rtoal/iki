package edu.lmu.cs.xlg.iki.entities;

import java.util.List;

import edu.lmu.cs.xlg.util.Log;

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

    @Override
    public void analyze(SymbolTable table, Log log) {
        for (Expression expression: expressions) {
            expression.analyze(table, log);
        }
    }
}
