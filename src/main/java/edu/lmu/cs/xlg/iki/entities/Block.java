package edu.lmu.cs.xlg.iki.entities;

import java.util.ArrayList;
import java.util.List;

import edu.lmu.cs.xlg.util.Log;

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

    @Override
    public void analyze(SymbolTable outer, Log log) {
        SymbolTable table = new SymbolTable(outer);
        for (Declaration d: declarations) {
            d.analyze(table, log);
        }
        for (Statement s: statements) {
            s.analyze(table, log);
        }
    }
}
