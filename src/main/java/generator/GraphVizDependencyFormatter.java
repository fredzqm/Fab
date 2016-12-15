package generator;

/**
 * A GraphVizDepenedency Formatter.
 * <p>
 * It provides common setup methods for Dependancy base relations.
 * <p>
 * Created by lamd on 12/14/2016.
 */
class GraphVizDependencyFormatter {
    static void setupDependencyVizDescription(StringBuilder vizDescription, String name) {
        final String VIZ_ARROW = " -> ";

        vizDescription.append(String.format("\"%s\"%s{", name, VIZ_ARROW));
    }

    static void closeDependencyVizDescription(StringBuilder vizDescription, int lengthBefore) {
        int length = vizDescription.length();

        // Ensure that it has changed.
        if (lengthBefore == length) {
            vizDescription.append("}");
        } else {
            vizDescription.replace(length - 2, length, "};\n");
        }
    }
}