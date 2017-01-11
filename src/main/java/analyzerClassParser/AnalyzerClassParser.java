package analyzerClassParser;

import analyzer.IAnalyzer;
import analyzer.IAnalyzerConfiguration;
import analyzer.ISystemModel;

/**
 * It uses {link GraphVizClassParser} to parse the label of all the classes in
 * system model
 *
 * @author zhang
 */
public class AnalyzerClassParser implements IAnalyzer {
    @Override
    public ISystemModel analyze(ISystemModel systemModel, IAnalyzerConfiguration config) {
        Object c = config.getConfigurationFor(AnalyzerClassParser.class);
        if (!(c instanceof IClassParserConfiguration)) {
            throw new RuntimeException(c + " is not a IClassParserConfiguration");
        }
        return new ParseClassSystemModel(systemModel, (IClassParserConfiguration) c);
    }
}
