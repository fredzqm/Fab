package adapter;

import adapter.classParser.ClassParserConfiguration;
import adapter.classParser.GraphvizClassParser;
import adapter.classParser.IParser;
import analyzer.utility.ClassPair;
import analyzer.utility.IClassModel;
import analyzer.utility.ISystemModel;
import analyzer.utility.StyleMap;
import config.IConfiguration;
import generator.IGraph;
import generator.INode;
import utility.IExpander;
import utility.IMapper;

import java.util.Map;

/**
 * An adapter from SystemModel to Graph Analyzers work on SystemModel, writing
 * style information there. This adapter would convert those information to what
 * Generator can understand
 *
 * @author zhang
 */
public class SystemModelGraph implements IGraph {
    private final ISystemModel systemModel;
    private ClassParserConfiguration classParserConfig;

    public SystemModelGraph(ISystemModel systemModel, IConfiguration config) {
        this.systemModel = systemModel;
        this.classParserConfig = config.createConfiguration(ClassParserConfiguration.class);
    }

    /**
     * Returns the vertices of the System Model. Class getClasses.
     *
     * @return Returns the classes of the System Model.
     */
    public Iterable<? extends INode> getNodes() {
        IParser<IClassModel> parser = new GraphvizClassParser();
        IMapper<IClassModel, INode> mapper = (c) -> new Node(c.getName(), parser.parse(c, systemModel, classParserConfig), systemModel.getNodeStyle(c));
        return mapper.map(systemModel.getClasses());
    }

    /**
     * Returns the Iterable of Relation edges.
     *
     * @return Iterable of Relation edges.
     */
    public Iterable<Relation> getEdges() {
        Map<ClassPair, Map<String, StyleMap>> relations = systemModel.getRelations();
        IExpander<ClassPair, Relation> expander = (key) -> {
            IMapper<StyleMap, Relation> mapper = (info) -> new Relation(key, info.getStyleString());
            return mapper.map(relations.get(key).values());
        };
        return expander.expand(relations.keySet());
    }
}
