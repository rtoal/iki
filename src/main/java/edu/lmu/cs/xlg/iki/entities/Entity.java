package edu.lmu.cs.xlg.iki.entities;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.lmu.cs.xlg.util.Log;

/**
 * An Iki entity.
 *
 * The front end of the compiler produces an intermediate representation in the form of a graph.
 * Each entity in the graph has some syntactic and semantic content.  The syntactic content is
 * filled in by the entity's constructor, and the semantic content is filled in by its analyze
 * method.
 *
 * The entities are naturally grouped into a hierarchy of classes:
 *
 * <pre>
 *     Program (block)
 *     Block (declarations, statements)
 *     Declaration
 *         Variable (name)
 *     Statement
 *         AssignmentStatement (variableReference, expression)
 *         ReadStatement (variableReferences)
 *         WriteStatement (expressions)
 *         WhileStatement (condition, body)
 *     Expression
 *         Number (value)
 *         VariableReference (name)
 *         BinaryExpression (operator, left, right)
 * </pre>
 */
public abstract class Entity {

    /**
     * Writes a simple, indented, syntax tree rooted at the given entity to the given print
     * writer.  Each level is indented two spaces.
     */
    public static void printSyntaxTree(String indent, String prefix, Entity e, PrintWriter out) {

        // Prepare the line to be written
        String className = e.getClass().getName();
        String line = indent + prefix + className.substring(className.lastIndexOf('.') + 1);

        // Process the fields, adding plain attributes to the line, but storing all the entity
        // children in a linked hash map to be processed after the line is written.  We use a
        // linked hash map because the order of output is important.
        Map<String, Entity> children = new LinkedHashMap<String, Entity>();
        for (Field field: Entity.relevantFields(e.getClass())) {
            try {
                field.setAccessible(true);
                String name = field.getName();
                Object value = field.get(e);
                if (value == null) {
                    continue;
                } else if (value instanceof Entity) {
                    children.put(name, (Entity)value);
                } else if (value instanceof Iterable<?>) {
                    try {
                        int index = 0;
                        for (Object child: (Iterable<?>)value) {
                            children.put(name + "[" + (index++) + "]", (Entity)child);
                        }
                    } catch (ClassCastException cce) {
                        // Special case for non-entity collections
                        line += " " + name + "=\"" + value + "\"";
                    }
                } else {
                    // Simple attribute, attach description to node name
                    line += " " + name + "=\"" + value + "\"";
                }
            } catch (IllegalAccessException cannotHappen) {
            }
        }
        out.println(line);
        for (Map.Entry<String, Entity> child: children.entrySet()) {
            printSyntaxTree(indent + "  ", child.getKey() + ": ", child.getValue(), out);
        }
    }

    /**
     * Traverses the semantic graph starting at e, applying visitor v to each entity.
     */
    public static void traverse(Entity e, Visitor v, Set<Entity> visited) {

        if (visited.contains(e)) {
            return;
        }
        visited.add(e);

        v.visit(e);

        for (Field field: Entity.relevantFields(e.getClass())) {
            try {
                field.setAccessible(true);
                Object value = field.get(e);
                if (value == null) {
                    continue;
                } else if (value instanceof Entity) {
                    traverse((Entity)value, v, visited);
                } else if (value instanceof Collection<?>) {
                    for (Object child: (Collection<?>)value) {
                        traverse((Entity)child, v, visited);
                    }
                }
            } catch (IllegalAccessException cannotHappen) {
            }
        }
    }

    public static interface Visitor {
        void visit(Entity e);
    }

    public static void dump(Entity root, PrintWriter writer) {
        writer.println("Semantic dump not implemented");
    }

    /**
     * Returns a list of all non-static declared fields of class c, together with the relevant
     * fields of its ancestor classes, assuming that c is a descendant class of Entity.
     */
    private static List<Field> relevantFields(Class<?> c) {
        List<Field> attributes = new ArrayList<Field>();
        for (Field field: c.getDeclaredFields()) {
            if ((field.getModifiers() & Modifier.STATIC) == 0) {
                attributes.add(field);
            }
        }
        if (c.getSuperclass() != Entity.class) {
            attributes.addAll(relevantFields(c.getSuperclass()));
        }
        return attributes;
    }

    /**
     * Performs semantic analysis on this entity.  Generally this operation updates fields in
     * the entity.  Sometimes it does nothing.  Sometimes it detects and logs errors.
     */
    public abstract void analyze(SymbolTable table, Log log);
}
