package model;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.modelmbean.ModelMBeanOperationInfo;

/**
 * The concrete ASM service provider that will recursively parse all related
 * classes request. {@see NonRecursiveASMParser}
 * 
 * @author zhang
 *
 */
public abstract class AbstractASMParser implements ASMServiceProvider {
	private Map<String, ClassModel> map = new HashMap<>();

	public AbstractASMParser(Iterable<String> importClassesList) {
		map = new HashMap<>();
		if (importClassesList != null) {
			for (String importantClass : importClassesList) {
				parseClass(importantClass);
			}
		}
	}

	protected ClassModel parseClass(String className) {
		className = className.replace(".", "/");
		if (map.containsKey(className))
			return map.get(className);
		try {
			ClassReader reader = new ClassReader(className);
			ClassNode classNode = new ClassNode();
			reader.accept(classNode, ClassReader.EXPAND_FRAMES);
			ClassModel model = new ClassModel(this, classNode, false);
			map.put(className, model);
			return model;
		} catch (IOException e) {
			throw new RuntimeException("ASM parsing of " + className + " failed.", e);
		}
	}

	@Override
	public String toString() {
		return map.toString();
	}

	public Iterable<ClassModel> getImportantClasses() {
		return new ArrayList(map.values());
	}
}
