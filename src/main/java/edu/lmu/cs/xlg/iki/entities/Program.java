package edu.lmu.cs.xlg.iki.entities;

import edu.lmu.cs.xlg.util.Log;

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

    @Override
    public void analyze(SymbolTable table, Log log) {
        block.analyze(table, log);
    }
}
