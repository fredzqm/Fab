package generator;

import java.util.Collection;

/**
 * Representing a single class in the DOT language.
 */
public class GraphVizClassParser implements IParser<IClassModel> {
    private IParser<IClassModel.IClassType> classTypeParser;
    private IParser<IFieldModel> fieldParser;
    private IParser<IMethodModel> methodParser;

    GraphVizClassParser(Collection<IModifier> filters) {
        this.classTypeParser = new GraphVizClassTypeParser();
        this.fieldParser = new GraphVizFieldParser(filters);
        this.methodParser = new GraphVizMethodParser(filters);
    }

    /**
     * Returns the String of the Class (header, fields, methods) in DOT file
     * format.
     *
     * @return Class in DOT format.
     */
    @Override
    public String parse(IClassModel model) {
        StringBuilder sb = new StringBuilder();
        String name = model.getName();
        IClassModel.IClassType classType = model.getType();

        // Set Description block.
        sb.append(String.format("\t\"%s\" [\n", name));

        // Set the header.
        sb.append(String.format("\t\tlabel = \"{%s%s | ", classTypeParser.parse(classType), formatName(name, classType)));

        // Set the fields.
        // Check to avoid double lines if there are no fields.
        Iterable<? extends IFieldModel> fields = model.getFields();
        if (fields.iterator().hasNext()) {
            sb.append(String.format("%s | ", fieldParser.parse(fields)));
        }

        // Set the methods.
        Iterable<? extends IMethodModel> methods = model.getMethods();
        if (methods.iterator().hasNext()) {
            sb.append(String.format("%s}\"", methodParser.parse(methods)));
        }
        sb.append("\n\t]\n");

        return sb.toString();
    }

    private String formatName(String name, IClassModel.IClassType type) {
        StringBuilder nameFormat = new StringBuilder();
        type.switchByCase(new IClassModel.IClassType.Switcher() {
            @Override
            public void ifInterface() {
                nameFormat.append(String.format("<I> %s <\\I>", name));
            }

            @Override
            public void ifConcrete() {
                nameFormat.append(name);
            }

            @Override
            public void ifAbstract() {
                nameFormat.append(String.format("<I> %s <\\I>", name));
            }

            @Override
            public void ifEnum() {
                nameFormat.append(name);
            }
        });

        return nameFormat.toString();
    }
}