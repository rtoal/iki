package edu.lmu.cs.xlg.iki.generators;

import java.io.Writer;

import edu.lmu.cs.xlg.iki.entities.Program;

/**
 * An interface for a generator that translates an Iki program into some other form.  The
 * result of the generation is written to a writer.
 */
public interface Generator {

    void generate(Program program, Writer writer);
}
