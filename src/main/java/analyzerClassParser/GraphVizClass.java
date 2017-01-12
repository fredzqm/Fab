package analyzerClassParser;

import analyzer.IClassModel;
import analyzer.IClassModelFilter;
import analyzer.IFieldModel;
import analyzer.IMethodModel;
import utility.IFilter;
import utility.Modifier;

import java.util.ArrayList;
import java.util.List;

public class GraphVizClass extends IClassModelFilter {
    private IClassParserConfiguration config;

    public GraphVizClass(IClassModel classModel, IClassParserConfiguration config) {
        super(classModel);
        this.config = config;
    }

    public String getLabel() {
        IFilter<Modifier> modifierFilter = config.getModifierFilters();
        IParser<IClassModel> header = config.getHeaderParser();
        IParser<IFieldModel> fieldParser = config.getFieldParser();
        IParser<IMethodModel> methodParser = config.getMethodParser();

        StringBuilder sb = new StringBuilder();
        // Set the header.

        sb.append(header.parse(this, config));
        // Filter the fields
        Iterable<? extends IFieldModel> fields = this.getFields();
        IFilter<IFieldModel> fieldFilters = (f) -> modifierFilter.filter(f.getModifier());
        fields = fieldFilters.filter(fields);
        // Render the fields
        if (fields.iterator().hasNext()) {
            sb.append(String.format(" | %s", fieldParser.parse(fields, config)));
        }

        // Filter the methods
        Iterable<? extends IMethodModel> methods = this.getMethods();
        IFilter<IMethodModel> methodFilters = (m) -> modifierFilter.filter(m.getModifier());
        methods = methodFilters.filter(methods);
        // Render the methods
        if (methods.iterator().hasNext()) {
            sb.append(String.format(" | %s", methodParser.parse(methods, config)));
        }

        // Generate the full string with the label text generated above.
        return sb.toString();
    }

    @Override
    public List<String> getStereoTypes() {
        List<String> ls = new ArrayList<>(1);
        switch (super.getType()) {
            case INTERFACE:
                ls.add("Interface");
                break;
            case CONCRETE:
                break;
            case ABSTRACT:
                ls.add("Abstract");
                break;
            case ENUM:
                ls.add("Enumeration");
                break;
        }
        return ls;
    }

}
