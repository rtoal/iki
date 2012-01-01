package edu.lmu.cs.xlg.iki.entities;

import java.util.List;

/**
 * An Iki read statement.
 */
public class ReadStatement extends Statement {

    private List<VariableReference> variables;

    public ReadStatement(List<VariableReference> variables) {
        this.variables = variables;
    }

    public List<VariableReference> getVariables() {
        return variables;
    }
}
