package edu.lmu.cs.xlg.iki.entities;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
 *         AssignmentStatement (variable-reference, expression)
 *         ReadStatement (variable-references)
 *         WriteStatement (expressions)
 *         WhileStatement (condition, body)
 *     Expression
 *         Number (value)
 *         VariableReference (name)
 *         BinaryExpression (operator, left, right)
 * </pre>
 */
public abstract class Entity {

    // TODO public abstract void analyze();

    /**
     * Writes a simple, indented, syntax tree rooted at the given entity to the given print
     * writer.  Each level is indented four spaces.
     */
    public static void printSyntaxTree(String indent, String prefix, Entity e, PrintWriter out) {

        // Build a node for it with the prefix and the class name
        String className = e.getClass().getName();
        Map<String, Entity> children = new LinkedHashMap<String, Entity>();
        String line = indent + prefix + className.substring(className.lastIndexOf('.') + 1);

        // Add all the children
        for (Field field: Entity.relevantFields(e.getClass())) {
            try {
                field.setAccessible(true);
                String name = field.getName();
                Object value = field.get(e);
                if (value == null) continue;
                if ((field.getModifiers() & Modifier.STATIC) != 0) continue;

                if (value instanceof Entity) {
                    children.put(name, (Entity)value);
                } else if (value instanceof Collection<?>) {
                    try {
                        int index = 0;
                        for (Object child: (Collection<?>)value) {
                            children.put(name + "[" + (index++) + "]", (Entity)child);
                        }
                    } catch (ClassCastException cce) {
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
     * Returns a list of all declared fields of class c, together with fields of its ancestor
     * classes, assuming that c is a descendant class of Entity.
     */
    private static List<Field> relevantFields(Class<?> c) {
        List<Field> attributes = new ArrayList<Field>();
        attributes.addAll(Arrays.asList(c.getDeclaredFields()));
        if (c.getSuperclass() != Entity.class) {
            attributes.addAll(relevantFields(c.getSuperclass()));
        }
        return attributes;
    }
}