package edu.lmu.cs.xlg.iki.entities;

/**
 * An Iki variable reference.  A variable reference is syntactically just an identifier.  During
 * semantic analysis we figure out the corresponding variable.
 */
public class VariableReference extends Expression {

    private String name;
    private Variable variable;

    public VariableReference(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Variable getVariable() {
        return variable;
    }

    // TODO analyze to set variable
}
