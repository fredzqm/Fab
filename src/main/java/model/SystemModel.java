package model;

import analyzer.IAnalyzerSystemModel;
import generator.IGeneratorSystemModel;

import java.util.ArrayList;
import java.util.List;

/**
 * This class representing the entire model of a java program
 * 
 */
public class SystemModel implements IGeneratorSystemModel, IAnalyzerSystemModel {
	private ASMServiceProvider asmServiceProvider;
	private List<ClassModel> importantClasses;

	public SystemModel(Iterable<String> classList, ASMServiceProvider asmParser) {
		asmServiceProvider = asmParser;
		importantClasses = new ArrayList<>();
		for (String className : classList) {
			importantClasses.add(asmServiceProvider.getClassByName(className));
		}
	}

	@Override
	public List<ClassModel> getClasses() {
		return importantClasses;
	}

	public static SystemModel getInstance(IModelConfiguration config) {
		ASMServiceProvider asmParser;
		if (config.isRecursive())
			asmParser = new ASMParser(config.getClasses());
		else
			asmParser = new NonRecursiveASMParser(config.getClasses());

		return new SystemModel(config.getClasses(), asmParser);
	}

}
