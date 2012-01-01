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
import edu.lmu.cs.xlg.util.Log;

/**
 * A compiler for Iki.
 */
/**
 * A Iki compiler. This class contains a static main method allowing you to run the compiler
 * from the command line, as well as a few methods to compile, or run phases of the compiler,
 * programmatically.
 */
public class Compiler {

    /**
     * A logger for logging messages (both regular and error messages). The base properties file is
     * called <code>Iki.properties</code>.
     */
    private Log log = new Log("Iki", new PrintWriter(System.out, true));

    /**
     * Runs the compiler as an application.
     *
     * @param args
     *            the commandline arguments. For now, the first argument should be the name of a
     *            file to compile; if missing, the compiler will read from standard input.
     */
    public static void main(String[] args) throws Exception {
        System.out.println("All I can do now is check syntax and semantics.");

        Compiler compiler = new Compiler();
        Reader reader = (args.length == 0) ? new BufferedReader(new InputStreamReader(System.in))
                : new FileReader(args[0]);
        Program program = compiler.checkSyntax(reader);

        if (program == null) {
            return;
        }

        // For now, print the syntax tree, but later we will want to do this only in a debug
        // mode or with some specific command line option.
        Entity.printSyntaxTree("", "", program, new PrintWriter(System.out, true));

//        Program.analyze(compiler.log);

        // TODO: Obviously, this is a stub.
    }

    /**
     * Checks the syntax of a Iki Program, given a reader object.
     *
     * @param reader
     *            the source
     * @return the abstract syntax tree if successful, or null if there were any syntax errors
     */
    public Program checkSyntax(Reader reader) throws IOException {
        log.clearErrors();

        Parser parser = new Parser(reader);
        try {
            log.message("syntax.checking");
            return parser.parse(log);
        } finally {
            reader.close();
        }
    }

    /**
     * Checks the static semantics of a Iki Program object, generally one already produced from
     * a parse.
     *
     * @param Program
     *            Program object to analyze
     * @return the (checked) semantic graph if successful, or null if there were any syntax or
     *         static semantic errors
     */
    public Program checkSemantics(Program program) {
        log.message("semantics.checking");
        //program.analyze(log);
        return program; // <----- TODO
    }

    /**
     * Checks the syntax and static semantics of a Iki program from a file.
     *
     * @param reader
     *            the source
     * @return the (checked) semantic graph if successful, or null if there were any syntax or
     *         static semantic errors
     */
    public Program checkSemantics(Reader reader) throws IOException {
        Program Program = checkSyntax(reader);
        if (log.getErrorCount() > 0) {
            return null;
        }
        return checkSemantics(Program);
    }

    /**
     * Returns the number of errors logged so far.
     */
    public int getErrorCount() {
        return log.getErrorCount();
    }

    /**
     * Tells the compiler whether or not it should write log messages.
     */
    public void setQuiet(boolean quiet) {
        log.setQuiet(quiet);
    }
}


