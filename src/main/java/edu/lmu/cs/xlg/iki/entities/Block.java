package edu.lmu.cs.xlg.iki.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * An Iki block.
 */
public class Block extends Entity {

    private List<Declaration> declarations = new ArrayList<Declaration>();
    private List<Statement> statements = new ArrayList<Statement>();

    public Block(List<Declaration> declarations, List<Statement> statements) {
        this.declarations = declarations;
        this.statements = statements;
    }

    public List<Declaration> getDeclarations() {
        return declarations;
    }

    public List<Statement> getStatements() {
        return statements;
    }
}
