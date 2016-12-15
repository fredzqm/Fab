package generator;

import java.util.Collection;

/**
 * A GraphVizParser for the Field class.
 *
 * Created by lamd on 12/14/2016.
 */
public class GFieldParser implements IParser<IFieldModel> {
	private Collection<IModifier> filters;

	public GFieldParser(Collection<IModifier> filters) {
		this.filters = filters;
	}

	@Override
	public String parse(IFieldModel field) {
		StringBuilder classFields = new StringBuilder();
		if (!filters.contains(field.getModifier())) {
			classFields.append(field.getModifier().getModifierSymbol());
			classFields.append(" ");
			classFields.append(field.getName());
			classFields.append(" : ");
			classFields.append(field.getType().getName());
			classFields.append("\\l");
		}
		return classFields.toString();
	}
}