package edu.lmu.cs.xlg.iki.entities;

/**
 * An Iki program.
 */
public class Program extends Entity {

    private Block block;

    public Program(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }
}
