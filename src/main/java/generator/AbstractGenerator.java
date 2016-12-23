package generator;

import java.util.Collection;

import generator.classParser.IClassModel;
import generator.classParser.IParser;
import generator.relParser.IParseGuide;

/**
 * An abstract class for Generators.
 * <p>
 * Created by lamd on 12/17/2016.
 */
public abstract class AbstractGenerator implements IGenerator {
	private final IParser<IClassModel> classModelParser;
	private final Collection<IParseGuide> relParsers;
	private final String basicConfiguration;

	AbstractGenerator(IGeneratorConfiguration config) {
		this.classModelParser = createClassParser(config);
		this.relParsers = createRelationshipParsers(config);
		this.basicConfiguration = createBasicConfiguration(config);
	}

	@Override
	public String generate(ISystemModel sm) {
		// DOT parent.
		Iterable<? extends IClassModel> classes = sm.getClasses();
		StringBuilder dotString = new StringBuilder();

		// Basic Configurations.
		dotString.append(basicConfiguration);

		// Parse the class
		dotString.append(classModelParser.parse(classes) + '\n');

		// Parse each relationship.
		this.relParsers.forEach((relParser) -> {
			StringBuilder sb = new StringBuilder();
			classes.forEach((thisClass) -> {
				Iterable<? extends IClassModel> otherClassList = relParser.getRelatesTo(thisClass);
				StringBuilder sb2 = new StringBuilder();
				otherClassList.forEach((has) -> {
					sb2.append(String.format("\"%s\" ", has.getName()));
				});
				sb.append(String.format("\t\"%s\" -> {%s};\n", thisClass.getName(), sb2.toString()));
			});

			dotString.append(String.format("\tedge [%s]\n%s\n", relParser.getEdgeStyle(), sb.toString()));
		});

		return String.format("digraph GraphVizGeneratedDOT {\n%s}", dotString.toString());
	}

	/**
	 * 
	 * @param config
	 * @return the basic configuration before all everything else
	 */
	public abstract String createBasicConfiguration(IGeneratorConfiguration config);

	/**
	 * Returns the class parser.
	 *
	 * @return ParseGuide of the Class.
	 */
	public abstract IParser<IClassModel> createClassParser(IGeneratorConfiguration config);

	/**
	 * Create super class, interfaces, has-a, and/or depends-on parsers.
	 *
	 * @return Collection of relationship ParseGuides
	 */
	public abstract Collection<IParseGuide> createRelationshipParsers(IGeneratorConfiguration config);
}
