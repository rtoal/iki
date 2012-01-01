package edu.lmu.cs.xlg.iki;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;

import edu.lmu.cs.xlg.iki.entities.Entity;
import edu.lmu.cs.xlg.iki.entities.Program;
import edu.lmu.cs.xlg.iki.syntax.Parser;

/**
 * A compiler for Iki.
 */
public class Compiler {

    /**
     * Compile the Iki program in the file whose name is the first argument, or from stdin if
     * no command line arguments are given.  TODO: command line options!
     */
    public static void main(String[] args) throws IOException {

        Reader reader = (args.length == 0) ? new BufferedReader(new InputStreamReader(System.in))
                : new FileReader(args[0]);

        Program program = new Parser(reader).parse();

        // For now, print the syntax tree, but later we will want to do this only in a debug
        // mode or with some specific command line option.
        Entity.printSyntaxTree("", "", program, new PrintWriter(System.out, true));

        // TODO semantic analysis

        // TODO generate target code
    }
}
