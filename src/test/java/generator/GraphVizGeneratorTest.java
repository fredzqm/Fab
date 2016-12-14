package generator;

import config.Configuration;
import model.ASMParser;
import model.Modifier;
import model.SystemModel;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import runner.GraphVizRunner;
import runner.IRunner;
import runner.IRunnerConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.Assert.*;

/**
 * The GraphVizGenerator and GraphVizRunner Test.
 * <p>
 * Created by lamd on 12/11/2016.
 */
public class GraphVizGeneratorTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private IGeneratorSystemModel setupSystemModel() {
        List<String> classList = new ArrayList<>();
        classList.add(DummyClass.class.getPackage().getName() + "." + DummyClass.class.getSimpleName());
        return new SystemModel(classList, new ASMParser());
    }

    @Test
    public void graphVizGenerate() throws IOException {
        // Set up the system model and config.
        IGeneratorSystemModel systemModel = setupSystemModel();
        IGeneratorConfiguration config = new DummyConfig();

        // Create GraphVizGenerator.
        IGenerator generator = new GraphVizGenerator();

        String actual = generator.generate(systemModel, config, null);

        // Test if it has the basic DOT file styling.
        assertTrue(actual.contains("\tnodesep=1.0;\n"));
        assertTrue(actual.contains("\tnode [shape=record];\n"));
        assertTrue(actual.contains("\t\"generator.DummyClass\" [\n"));
        assertTrue(actual.contains("\t\"generator.DummyClass\" -> {\"java.lang.Object\"};\n"));
        assertTrue(actual.contains("\t\"generator.DummyClass\" -> {}\n"));
        assertTrue(actual.contains("edge [arrowhead=vee style=dashed]"));
        assertTrue(actual.contains("edge [arrowhead=onormal]"));
        assertTrue(actual.contains("\"generator.DummyClass\" -> {\"java.lang.Object\"}"));

        // Count how many relations there are.
        // TODO: When Fred implements Has-A and Depends-On update this test.
        int relationsCount = 0;
        int index = actual.indexOf("\t\"generator.DummyClass\" -> {}\n");
        while (index != -1) {
            relationsCount++;
            index = actual.indexOf("\t\"generator.DummyClass\" -> {}\n", index + 1);
        }
        assertEquals("Number of Relations not equal", 3, relationsCount);

        String[] expectedFields = {"- privateInt : int", "+ publicString : java.lang.String",
                "- privateString : java.lang.String", "+ publicInt : int"};
        String[] expectedMethods = {"- printPrivateString() : void", "getPublicInt() : int",
                "+ getPublicString() : java.lang.String", "# someProtectedMethod() : double"};

        Stream<String> expectedFieldStream = Arrays.stream(expectedFields);
        Stream<String> expectedMethodStream = Arrays.stream(expectedMethods);

        // Test if it has the Fields viewable in the class file.
        expectedFieldStream.forEach((field) -> assertTrue(actual.contains(field)));

        // Test if it has the Methods viewable in the class file.
        expectedMethodStream.forEach((method) -> assertTrue(actual.contains(method)));
    }

    @Test
    public void graphVizGeneratorFilter() {
        // Set up the system model and config.
        IGeneratorSystemModel systemModel = setupSystemModel();

        DummyConfig config = new DummyConfig();
        config.addFilter(Modifier.PROTECTED);
        config.addFilter(Modifier.PRIVATE);

        // Create GraphVizGenerator.
        IGenerator generator = new GraphVizGenerator();

        String actual = generator.generate(systemModel, config, null);

        // Test if it has the basic DOT file styling.
        assertTrue(actual.contains("\tnodesep=1.0;\n"));
        assertTrue(actual.contains("\tnode [shape=record];\n"));
        assertTrue(actual.contains("\t\"generator.DummyClass\" [\n"));
        assertTrue(actual.contains("\t\"generator.DummyClass\" -> {\"java.lang.Object\"};\n"));
        assertTrue(actual.contains("\t\"generator.DummyClass\" -> {}\n"));
        assertTrue(actual.contains("edge [arrowhead=vee style=dashed]"));
        assertTrue(actual.contains("edge [arrowhead=onormal]"));
        assertTrue(actual.contains("\"generator.DummyClass\" -> {\"java.lang.Object\"}"));

        // Count how many relations there are.
        // TODO: When Fred implements Has-A and Depends-On update this test.
        int relationsCount = 0;
        int index = actual.indexOf("\t\"generator.DummyClass\" -> {}\n");
        while (index != -1) {
            relationsCount++;
            index = actual.indexOf("\t\"generator.DummyClass\" -> {}\n", index + 1);
        }
        assertEquals("Number of Relations not equal", 3, relationsCount);

        String[] expectedFields = {"+ publicString : java.lang.String", "+ publicInt : int"};
        String[] expectedMethods = {"getPublicInt() : int",
                "+ getPublicString() : java.lang.String"};

        Stream<String> expectedFieldStream = Arrays.stream(expectedFields);
        Stream<String> expectedMethodStream = Arrays.stream(expectedMethods);

        // Test if it has the Fields viewable in the class file.
        expectedFieldStream.forEach((field) -> assertTrue(actual.contains(field)));

        // Test if it has the Methods viewable in the class file.
        expectedMethodStream.forEach((method) -> assertTrue(actual.contains(method)));
        }

    @Test
    public void graphVizWrite() throws IOException {
        // Create a TemporaryFolder that will be deleted after the test runs.
        File directory = this.folder.newFolder("testDirectory");

        // Set up a System Model.
        IGeneratorSystemModel systemModel = setupSystemModel();
        DummyConfig config = new DummyConfig();

        // Set the output directory to the root of the Temporary Folder.
        config.setOutputDirectory(directory.toString());

        // Create the runner
        IRunner runner = new GraphVizRunner();
        IGenerator generator = new GraphVizGenerator();
        String graphVizString = generator.generate(systemModel, config, null);

        try {
            runner.execute(config, graphVizString);
            File file = new File(directory, config.getFileName() + "." + config.getOutputFormat());
            assertTrue(file.exists());
        } catch (Exception e) {
            fail("[ ERROR ]: An Exception has occured!\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * A Dummy Configuration Object used for testing.
     */
    private class DummyConfig implements IRunnerConfiguration, IGeneratorConfiguration {
        String executablePath;
        String fileName, outputDirectory, outputFormat;
        double nodeSep;
        Collection<IModifier> filters;

        /**
         * Constructs a basic DummyConfig object.
         */
        DummyConfig() {
            System.out.println("[ INFO ]: Make sure that the GraphViz bin is in your environment variables.");
            this.outputDirectory = "./output";
            this.fileName = "test";
            this.outputFormat = "png";
            this.nodeSep = 1.0;
            this.executablePath = "dot";
            this.filters = new HashSet<>();
        }

        @Override
        public String getFileName() {
            return this.fileName;
        }

        public void setFileName(String name) {
            this.fileName = name;
        }

        @Override
        public String getOutputDirectory() {
            return this.outputDirectory;
        }

        public void setOutputDirectory(String outputDirectory) {
            this.outputDirectory = outputDirectory;
        }

        @Override
        public String getOutputFormat() {
            return this.outputFormat;
        }

        public void setOutputFormat(String outputFormat) {
            this.outputFormat = outputFormat;
        }

        @Override
        public String getExecutablePath() {
            return this.executablePath;
        }

        public void setExecutablePath(String executablePath) {
            this.executablePath = executablePath;
        }

        @Override
        public double getNodeSep() {
            return this.nodeSep;
        }

        @Override
        public Collection<IModifier> getFilters() {
            return this.filters;
        }

        public void setNodeSep(double nodeSep) {
            this.nodeSep = nodeSep;
        }

        public void addFilter(Modifier filter) {
            this.filters.add(filter);
        }
    }
}