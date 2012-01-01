package edu.lmu.cs.xlg.iki.generators;

import java.io.PrintWriter;

import edu.lmu.cs.xlg.iki.entities.Program;

/**
 * An generator that translates an Iki program into some other form.  The result of the translation
 * is written to a writer.
 */
public abstract class Generator {

    /**
     * A factory for retrieving a specific generator based on a name.  TODO: This is ugly
     * because the generators and their names are hardcoded.  Needs to be refactored so the
     * actual generators register themselves.
     */
    public static Generator getGenerator(String name) {
        if ("c".equals(name)) {
            return new IkiToCGenerator();
        } else if ("js".equals(name)) {
            return new IkiToJavaScriptGenerator();
        } else if ("86".equals(name)) {
            return new IkiToX86Generator();
        } else {
            return null;
        }
    }

    /**
     * @param program
     * @param writer
     */
    public abstract void generate(Program program, PrintWriter writer);
}
