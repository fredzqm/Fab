package generator;

import java.util.Collection;

/**
 * A GraphVizParser for the model's SuperClass.
 *
 * Created by lamd on 12/14/2016.
 */
public class GSuperClassRelParser implements IParser<IClassModel> {
	private Collection<IModifier> filters;

	public GSuperClassRelParser(Collection<IModifier> filters) {
		this.filters = filters;
	}

	@Override
	public String parse(IClassModel thisClass) {
		IClassModel superClass = thisClass.getSuperClass();
		if (superClass == null || filters.contains(superClass.getModifier())) {
			return "";
		}
		StringBuilder sb = new StringBuilder();

		GDependencyFormatter.setupDependencyVizDescription(sb, thisClass.getName());
		sb.append("\"").append(superClass.getName()).append("\"");
		sb.append("};\n");
		sb.append("\n\t");
		return sb.toString();
	}

}