package app;

import analyzer.classParser.AnalyzerClassParser;
import analyzer.favorComposition.FavorCompositionAnalyzer;
import analyzer.relationParser.AnalyzerRelationParser;
import analyzer.utility.ISystemModel;
import config.Configuration;
import dummy.favorDummy.FavorDummyA;
import dummy.favorDummy.FavorDummyB;
import generator.GeneratorConfiguration;
import model.ModelConfiguration;
import runner.RunnerConfiguration;
import viewer.Viewer;

/**
 * A Test Class that will generate files for local inspection.
 * <p>
 * Created by lamd on 12/15/2016.
 */
public class LocalTester {
    public static void main(String[] args) {
        // Set up the config.
        Configuration config = Configuration.getInstance();
        config.set(ModelConfiguration.IS_RECURSIVE_KEY, Boolean.toString(false));
        config.set(GeneratorConfiguration.NODE_SEP, Double.toString(1.0));
        config.set(GeneratorConfiguration.RANK_DIR, ("BT"));
        config.set(RunnerConfiguration.OUTPUT_DIRECTORY, "./output");
        config.set(RunnerConfiguration.FILE_NAME, "asmClass");
        config.set(RunnerConfiguration.EXECUTABLE_PATH, "dot");
        config.set(RunnerConfiguration.OUTPUT_FORMAT, "svg");
        config.add(EngineConfiguration.ANALYZER_KEY, FavorCompositionAnalyzer.class.getName(), AnalyzerClassParser.class.getName(),AnalyzerRelationParser.class.getName());
        config.add(ModelConfiguration.CLASSES_KEY, FavorDummyA.class.getName(), FavorDummyB.class.getName());

        UMLEngine engine = UMLEngine.getInstance(config);
        ISystemModel systemModel = engine.createSystemModel();
        systemModel = engine.analyze(systemModel);

        System.out.println(systemModel.getRelations());

        String actual = engine.generate(systemModel);
        engine.executeRunner(actual);

        Runnable explorerRunner = new Viewer(config.createConfiguration(RunnerConfiguration.class));
        explorerRunner.run();

        System.out.println("Done");
    }

}
