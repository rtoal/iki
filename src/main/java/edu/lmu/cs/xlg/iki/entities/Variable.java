package edu.lmu.cs.xlg.iki.entities;

/**
 * An Iki variable.  Variables are distinct from variable references.  A variable is something
 * that is declared.  A variable reference is a kind of expression.  Iki variable declarations
 * are very simple.  They don't have types nor initial values.  They are a rather boring kind
 * of declaration.
 */
public class Variable extends Declaration {

    public Variable(String name) {
        super(name);
    }
}
