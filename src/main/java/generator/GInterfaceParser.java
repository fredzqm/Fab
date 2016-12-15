package generator;

/**
 * A GraphVizParser for the model's interface.
 *
 * Created by lamd on 12/14/2016.
 */
public class GInterfaceParser implements IParser<IClassModel> {

	@Override
	public String parse(IClassModel thisClass) {
		Iterable<? extends IClassModel> otherClassLs = thisClass.getInterfaces();

		StringBuilder sb = new StringBuilder();
		GDependencyFormatter.setupDependencyVizDescription(sb, thisClass.getName());
		int interfaceLengthBefore = sb.length();

		otherClassLs.forEach((interfaceModel) -> {
			sb.append("\"").append(interfaceModel.getName()).append("\"");
			sb.append(", ");
		});
		// If it is empty close the braces without replacing characters.
		GDependencyFormatter.closeDependencyVizDescription(sb, interfaceLengthBefore);
		sb.append("\n\t");
		return sb.toString();
	}

}