package ru.timeconqueror.timecore.api.util;

public class SimpleName {
    public static String of(Class<?> clazz) {
        return simpleNameWithClassHierarchy(clazz);
    }

    public static String snakeCasedOf(Class<?> clazz) {
        return convertClassNameToSnake(simpleNameWithClassHierarchy(clazz));
    }

    /**
     * Returns the simple class name or the full hierarchy of enclosing classes
     * for the given {@code Class<?>}, without including package names.
     * <p>
     * If the class is a top-level class, this simply returns its simple name.
     * If the class is nested or an inner class, the returned string represents
     * the full hierarchy of outer classes separated by '$' sign, ending with the
     * innermost class.
     * </p>
     *
     * <p>Examples:</p>
     * <pre>
     * class Outer {
     *     class Inner {
     *         class InnerMost {}
     *     }
     * }
     *
     * getSimpleClassHierarchy(Outer.class)                  → "Outer"
     * getSimpleClassHierarchy(Outer.Inner.class)           → "Outer$Inner"
     * getSimpleClassHierarchy(Outer.Inner.InnerMost.class) → "Outer$Inner$InnerMost"
     * </pre>
     *
     * @param clazz the {@link Class} object to get the simple name hierarchy for
     * @return a {@link String} representing the simple name or nested hierarchy of the class, without package names;
     *         returns an empty string if {@code clazz} is {@code null}
     */
    public static String simpleNameWithClassHierarchy(Class<?> clazz) {
        if (clazz == null) return "";

        // We'll build the hierarchy from outer to inner
        StringBuilder sb = new StringBuilder();
        Class<?> current = clazz;

        // Collect names in reverse order (inner first)
        while (current != null) {
            if (sb.isEmpty()) {
                sb.insert(0, current.getSimpleName());
            } else {
                sb.insert(0, current.getSimpleName() + "$");
            }
            current = current.getEnclosingClass();
        }

        return sb.toString();
    }

    /**
     * Converts a class name string to a snake-case path string.
     * <p>
     * Rules:
     * <ul>
     *     <li>Outer class names are converted to lower-case snake case.</li>
     *     <li>Inner classes (denoted by '$') are separated by '/'.</li>
     *     <li>CamelCase is converted to snake_case.</li>
     * </ul>
     *
     * <p>Examples:</p>
     * <pre>
     * convertClassNameToSnake("Outer")            → "outer"
     * convertClassNameToSnake("SimpleName")       → "simple_name"
     * convertClassNameToSnake("Outer$Inner")      → "outer/inner"
     * convertClassNameToSnake("Outer$SimpleName") → "outer/simple_name"
     * </pre>
     *
     * @param className the fully-qualified or simple class name string (can include '$' for inner classes)
     * @return a snake-case string with inner classes separated by '/'
     */
    public static String convertClassNameToSnake(String className) {
        if (className == null || className.isEmpty()) return "";

        // Split on inner class '$'
        String[] parts = className.split("\\$");

        // Convert each part to snake_case
        for (int i = 0; i < parts.length; i++) {
            parts[i] = toSnakeCase(parts[i]);
        }

        // Join with '/'
        return String.join("/", parts);
    }

    /**
     * Converts a single CamelCase word to snake_case.
     *
     * @param input the CamelCase string
     * @return the snake_case string
     */
    private static String toSnakeCase(String input) {
        // Insert underscore before each uppercase letter (except the first), then lowercase
        return input.replaceAll("([a-z0-9])([A-Z])", "$1_$2")
                .replaceAll("([A-Z])([A-Z][a-z])", "$1_$2")
                .toLowerCase();
    }
}
