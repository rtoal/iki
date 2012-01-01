package edu.lmu.cs.xlg.iki.entities;

/**
 * An Iki numeric literal expression.
 */
public class Number extends Expression {

    private int value;

    public Number(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
