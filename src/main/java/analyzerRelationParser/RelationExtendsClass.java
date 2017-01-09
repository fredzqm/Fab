package analyzerRelationParser;

import analyzer.IRelationInfo;

public class RelationExtendsClass implements IRelationInfo {
	@Override
	public String toString() {
		return "extends";
	}

	@Override
	public String getEdgeStyle() {
		return "arrowhead=\"onormal\" style=\"\" ";
	}
}
