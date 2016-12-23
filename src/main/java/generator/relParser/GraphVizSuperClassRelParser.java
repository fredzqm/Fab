package generator.relParser;

/**
 * A GraphVizParser for the model's SuperClass.
 * <p>
 * Created by lamd on 12/14/2016.
 */
public class GraphVizSuperClassRelParser implements IParseGuide {

    @Override
    public String getEdgeStyle(Relation edge) {
        return "arrowhead=onormal style=\"\" ";
    }

}