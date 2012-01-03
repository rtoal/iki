package edu.lmu.cs.xlg.iki.generators;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import edu.lmu.cs.xlg.iki.entities.AssignmentStatement;
import edu.lmu.cs.xlg.iki.entities.BinaryExpression;
import edu.lmu.cs.xlg.iki.entities.Block;
import edu.lmu.cs.xlg.iki.entities.Expression;
import edu.lmu.cs.xlg.iki.entities.Number;
import edu.lmu.cs.xlg.iki.entities.Program;
import edu.lmu.cs.xlg.iki.entities.ReadStatement;
import edu.lmu.cs.xlg.iki.entities.Statement;
import edu.lmu.cs.xlg.iki.entities.VariableReference;
import edu.lmu.cs.xlg.iki.entities.WhileStatement;
import edu.lmu.cs.xlg.iki.entities.WriteStatement;

/**
 * A generator that translates an Iki program into assembly language for the x86-84, good
 * only for Linux.  Doesn't work on the Mac, since it doesn't do that %rip-relative stuff.
 */
public class IkiToX86Generator extends Generator {

    private RegisterAllocator allocator = new RegisterAllocator();
    private Set<String> usedVariables = new HashSet<String>();

    @Override
    public void generate(Program program, PrintWriter writer) {
        this.writer = writer;

        emitProgramPrologue();
        generateBlock(program.getBlock());
        emitProgramEpilogue();
    }

    // Translation Functions - one for each entity.

    private void generateBlock(Block block) {
        for (Statement s : block.getStatements()) {
            generateStatement(s);
            allocator.freeAllRegisters();
        }
    }

    private void generateStatement(Statement s) {
        if (s instanceof AssignmentStatement) {
            generateAssignmentStatement((AssignmentStatement) s);
        } else if (s instanceof ReadStatement) {
            generateReadStatement((ReadStatement) s);
        } else if (s instanceof WriteStatement) {
            generateWriteStatement((WriteStatement) s);
        } else if (s instanceof WhileStatement) {
            generateWhileStatement((WhileStatement) s);
        } else {
            throw new RuntimeException("Bug inside compiler");
        }
    }

    private void generateAssignmentStatement(AssignmentStatement s) {
        Operand source = generateExpression(s.getExpression());
        Operand destination = generateVariableReference(s.getVariableReference());
        if (source instanceof MemoryOperand && destination instanceof MemoryOperand) {
            Operand oldSource = source;
            source = new RegisterOperand(allocator.getRegister());
            emitMove(oldSource, source);
        }
        emitMove(source, destination);
    }

    private void generateReadStatement(ReadStatement s) {
        for (VariableReference r : s.getReferences()) {
            emitRead(generateVariableReference(r));
        }
    }

    private void generateWriteStatement(WriteStatement s) {
        for (Expression e : s.getExpressions()) {
            emitWrite(generateExpression(e));
        }
    }

    private void generateWhileStatement(WhileStatement s) {
        Label top = new Label();
        Label bottom = new Label();
        emitLabel(top);
        Operand condition = generateExpression(s.getCondition());
        emitJumpIfFalse(condition, bottom);
        allocator.freeAllRegisters();
        generateBlock(s.getBody());
        emitJump(top);
        emitLabel(bottom);
    }

    private Operand generateExpression(Expression e) {
        if (e instanceof Number) {
            return generateNumber((Number) e);
        } else if (e instanceof VariableReference) {
            return generateVariableReference((VariableReference) e);
        } else if (e instanceof BinaryExpression) {
            return generateBinaryExpression((BinaryExpression) e);
        } else {
            throw new RuntimeException("Bug inside compiler");
        }
    }

    private Operand generateNumber(Number number) {
        return new ImmediateOperand(number.getValue());
    }

    private MemoryOperand generateVariableReference(VariableReference variable) {
        String name = id(variable);
        usedVariables.add(name);
        return new MemoryOperand(name);
    }

    private Operand generateBinaryExpression(BinaryExpression e) {
        Operand left = generateExpression(e.getLeft());
        Operand right = generateExpression(e.getRight());
        Operand result;
        if (left instanceof RegisterOperand) {
            result = left;
        } else {
            result = new RegisterOperand(allocator.getRegister());
            emitMove(left, result);
        }
        switch (e.getOperator()) {
        case PLUS: emitBinary("add", right, result);
        case MINUS: emitBinary("sub", right, result);
        case TIMES: emitBinary("mul", right, result);
        case DIVIDE: emit("# Divide is not yet supported");
        }
        return result;
    }

    // Assembly Language Specific part of the Generator.
    // To target a new x86 assembler, override the methods
    // from here down.

    /**
     * Emits the assembly language that goes at the beginning.
     */
    protected void emitProgramPrologue() {
        emit("\t.globl\t_main");
        emit("\t.text");
        emit("_main:");
    }

    /**
     * Emits the assembly language that goes at the end. We need a return instruction at the end of
     * the code, then a data section which declares format strings for scanf() and printf(), then
     * data declarations for all the variables that were declared. Here is where we handle the
     * requirement that all variables are initialized to zero.
     */
    protected void emitProgramEpilogue() {
        emit("\tret");
        emit("\t.data");
        emit("READ:\t.ascii\t\"%d\\0\\0\""); // extra 0 for alignment
        emit("WRITE:\t.ascii\t\"%d \\0\"");
        for (String s: usedVariables) {
            emit(s + ":\t.quad\t0");
        }
    }

    /**
     * Emits a label on its own line.
     */
    protected void emitLabel(Label label) {
        emit(label + ":");
    }

    /**
     * Emits a single move instruction.
     */
    protected void emitMove(Operand source, Operand destination) {
        emit("\tmov\t" + source + ", " + destination);
    }

    /**
     * Emits a single unconditional jump instruction.
     */
    protected void emitJump(Label label) {
        emit("\tjmp\t" + label);
    }

    /**
     * Emits code to jump to a label if a given expression is 0. On the x86 this is done with a
     * comparison instruction and then a je instruction. The cmp instruction cannot compare two
     * immediate values, so if the operand is immediate we have to get a new register for it.
     */
    protected void emitJumpIfFalse(Operand operand, Label label) {
        if (operand instanceof ImmediateOperand) {
            Operand oldOperand = operand;
            operand = new RegisterOperand(allocator.getRegister());
            emit("\tmovq\t" + oldOperand + ", " + operand);
        }
        emit("\tcmp\t$0, " + operand);
        emit("\tje\t" + label);
    }

    /**
     * Emits a binary instruction.
     */
    protected void emitBinary(String instruction, Operand source, Operand destination) {
        emit("\t" + instruction + "\t" + source + ", " + destination);
    }

    /**
     * Generates code to call scanf() from the C library for the given operand. Requires the
     * operand to be in %rsi and the format string in %rdi.
     */
    protected void emitRead(MemoryOperand operand) {
        emit("\tmov\t" + operand.address() + ", %rsi");
        emit("\tmov\t$READ, %rdi");
        emit("\txor\t%rax, %rax");
        emit("\tcall\t_scanf");
    }

    /**
     * Generates code to call printf() from the C library for the given operand. Requires the
     * operand to be in %rsi and the format string in %rdi.
     */
    protected void emitWrite(Operand operand) {
        emit("\tmov\t" + operand + ", %rsi");
        emit("\tmov\t$WRITE, %rdi");
        emit("\txor\t%rax, %rax");
        emit("\tcall\t_printf");
    }
}

/**
 * A ridiculously simple register allocator. Call <code>getRegister()</code> to get the name of a
 * free register, or have an exception thrown if there are no free registers available. You can't
 * mark individual registers free; you can only call freeAllRegisters().
 */
class RegisterAllocator {
    private int numberOfRegistersInUse = 0;

    private static String[] names = { "rax", "rdx", "rcx", "r8", "r9", "r10", "r11"};

    public String getRegister() {
        if (numberOfRegistersInUse == names.length) {
            throw new RuntimeException("No more registers available");
        }
        return names[numberOfRegistersInUse++];
    }

    public void freeAllRegisters() {
        numberOfRegistersInUse = 0;
    }
}

/**
 * Assembly language labels.
 */
class Label {
    private static int numberOfLabelsGenerated = 0;
    private int labelNumber;

    public Label() {
        labelNumber = ++numberOfLabelsGenerated;
    }

    public String toString() {
        return "L" + labelNumber;
    }
}

/**
 * Assembly language operands.
 */
abstract class Operand {
}

/**
 * Assembly language immediate operands, e.g. 4.
 */
class ImmediateOperand extends Operand {
    private int value;

    public ImmediateOperand(int value) {
        this.value = value;
    }

    public String toString() {
        return "$" + value;
    }
}

/**
 * Assembly language register operands, e.g. esi.
 */
class RegisterOperand extends Operand {
    private String register;

    public RegisterOperand(String register) {
        this.register = register;
    }

    public String toString() {
        return "%" + register;
    }
}

/**
 * Assembly language memory operands. Although the x86 has a variety of addressing modes, Iki
 * programs require only direct operands. All Iki variables will be stored in a data section. The
 * assembly language name of a variable is its Iki name suffixed with a '$' (to prevent clashes with
 * assembly language reserved words).
 */
class MemoryOperand extends Operand {
    private String variable;

    public MemoryOperand(String variable) {
        this.variable = variable;
    }

    public String address() {
        return "$" + variable;
    }

    public String toString() {
        return variable;
    }
}
