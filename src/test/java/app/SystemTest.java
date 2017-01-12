package app;

import analyzer.ClassPair;
import analyzer.IClassModel;
import analyzer.IRelationInfo;
import analyzer.ISystemModel;
import analyzerRelationParser.RelationDependsOn;
import analyzerRelationParser.RelationExtendsClass;
import analyzerRelationParser.RelationImplement;
import config.Configuration;
import config.GeneratorConfiguration;
import config.ModelConfiguration;
import config.RunnerConfiguration;
import dummy.generic.GenDummyClass;
import dummy.hasDependsRel.RelDummyClass;
import dummy.hasDependsRel.RelDummyManyClass;
import dummy.hasDependsRel.RelOtherDummyClass;
import dummy.inheritanceRel.DummyInterface;
import dummy.inheritanceRel.DummySubClass;
import dummy.inheritanceRel.DummySuperClas;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import utility.IFilter;
import utility.Modifier;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.Assert.*;

/**
 * The GraphVizGenerator and GraphVizRunner Test.
 * <p>
 * Created by lamd on 12/11/2016.
 */
public class SystemTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private String dummyClassName = GenDummyClass.class.getName();

    @Test
    public void graphVizGenerate() throws IOException {
        // Set up the config.
        Configuration config = Configuration.getInstance();
        config.add(ModelConfiguration.CLASSES_KEY, dummyClassName);
        config.set(ModelConfiguration.IS_RECURSIVE_KEY, "true");
        config.set(GeneratorConfiguration.NODE_SEP, "1.0");
        config.set(GeneratorConfiguration.RANK_DIR, "BT");

        AbstractUMLEngine engine = UMLEngine.getInstance(config);
        ISystemModel systemModel = engine.createSystemModel();
        systemModel = engine.analyze(systemModel);
        String actual = engine.generate(systemModel);

        // Test if it has the basic DOT file styling.
        assertTrue("Missing nodesep.", actual.contains("nodesep=1.0;"));
        assertTrue("Missing node shape.", actual.contains("node [shape=record];"));
        assertTrue("Missing rankdir.", actual.contains("rankdir=BT"));
        assertTrue("Missing primary class name.", actual.contains(String.format("\"%s\"", dummyClassName)));

        // See if it has its expected super class.
        String expectedSuperClass = String.format("\"%s\" -> \"java.lang.Object\" [arrowhead=\"onormal\" style=\"\" ];",
                dummyClassName);
        assertTrue("Missing super class relation.", actual.contains(expectedSuperClass));

        // See if it has its expected dependencies.
        String expectedDependencies = String.format(
                "\"%s\" -> \"java.lang.String\" [arrowhead=\"vee\" style=\"\" taillabel=\"2\" ];", dummyClassName);
        assertTrue("Missing dependency relations.", actual.contains(expectedDependencies));

        // Check expected fields and methods.
        String[] expectedFields = {"- privateInt : int", "+ publicString : java.lang.String",
                "- privateString : java.lang.String", "+ publicInt : int"};
        String[] expectedMethods = {"- printPrivateString() : void", "getPublicInt() : int",
                "+ getPublicString() : java.lang.String", "# someProtectedMethod() : double"};

        Stream<String> expectedFieldStream = Arrays.stream(expectedFields);
        Stream<String> expectedMethodStream = Arrays.stream(expectedMethods);

        // Test if it has the Fields viewable in the class file.
        expectedFieldStream.forEach(
                (field) -> assertTrue(String.format("Missing expected field: %s", field), actual.contains(field)));

        // Test if it has the Methods viewable in the class file.
        expectedMethodStream.forEach(
                (method) -> assertTrue(String.format("Missing expected method: %s", method), actual.contains(method)));
    }

    @Test
    public void graphVizGeneratorFilter() {
        // Set up the config.
        Configuration config = Configuration.getInstance();
        config.add(ModelConfiguration.CLASSES_KEY, dummyClassName);
        config.set(ModelConfiguration.IS_RECURSIVE_KEY, "true");
        config.set(GeneratorConfiguration.NODE_SEP, "1.0");
        config.set(GeneratorConfiguration.RANK_DIR, "BT");
        IFilter<Modifier> filter = data -> data == Modifier.DEFAULT || data == Modifier.PUBLIC;

        config.setFilter(filter);

        // Set up the system model and generator.
        AbstractUMLEngine engine = UMLEngine.getInstance(config);
        ISystemModel systemModel = engine.createSystemModel();
        systemModel = engine.analyze(systemModel);
        String actual = engine.generate(systemModel);

        // Test if it has the basic DOT file styling.
        assertTrue("Missing nodesep.", actual.contains("nodesep=1.0;"));
        assertTrue("Missing node shape.", actual.contains("node [shape=record];"));
        assertTrue("Missing rankdir.", actual.contains("rankdir=BT"));
        assertTrue("Missing primary class name.", actual.contains(String.format("\"%s\"", dummyClassName)));

        // See if it has its expected super class.
        String expectedSuperClass = String.format("\"%s\" -> \"java.lang.Object\" [arrowhead=\"onormal\" style=\"\" ];",
                dummyClassName);
        assertTrue("Missing super class relation.", actual.contains(expectedSuperClass));

        // See if it has its expected dependencies.
        String expectedDependencies = String.format(
                "\"%s\" -> \"java.lang.String\" [arrowhead=\"vee\" style=\"\" taillabel=\"2\" ];", dummyClassName);
        assertTrue("Missing dependency relations.", actual.contains(expectedDependencies));

        // Set up expected fields and methods.
        String[] expectedFields = {"+ publicString : java.lang.String", "+ publicInt : int"};
        String[] expectedMethods = {"getPublicInt() : int", "+ getPublicString() : java.lang.String"};
        Stream<String> expectedFieldStream = Arrays.stream(expectedFields);
        Stream<String> expectedMethodStream = Arrays.stream(expectedMethods);

        // Test if it has the Fields viewable in the class file.
        expectedFieldStream.forEach(
                (field) -> assertTrue(String.format("Missing expected field: %s", field), actual.contains(field)));

        // Test if it has the Methods viewable in the class file.
        expectedMethodStream.forEach(
                (method) -> assertTrue(String.format("Missing expected method: %s", method), actual.contains(method)));
    }

    @Test
    public void graphVizWrite() throws IOException {
        // Create a TemporaryFolder that will be deleted after the test runs.
        File directory = this.folder.newFolder("testDirectory");

        // Set up config.
        Configuration config = Configuration.getInstance();
        config.add(ModelConfiguration.CLASSES_KEY, dummyClassName);
        config.set(ModelConfiguration.IS_RECURSIVE_KEY, "false");
        config.set(GeneratorConfiguration.NODE_SEP, "1.0");
        config.set(GeneratorConfiguration.RANK_DIR, "BT");
        config.set(RunnerConfiguration.FILE_NAME, "testWrite");
        config.set(RunnerConfiguration.OUTPUT_FORMAT, "svg");
        config.set(RunnerConfiguration.EXECUTABLE_PATH, "dot");
        config.set(RunnerConfiguration.OUTPUT_DIRECTORY, directory.toString());

        // Set up a System Model.
        AbstractUMLEngine engine = UMLEngine.getInstance(config);
        ISystemModel systemModel = engine.createSystemModel();
        systemModel = engine.analyze(systemModel);
        String graphVizString = engine.generate(systemModel);
        engine.executeRunner(graphVizString);
    }

    @Test
    public void testOverImplementing() throws IOException {
        // Set up config.
        Configuration config = Configuration.getInstance();
        config.add(ModelConfiguration.CLASSES_KEY, DummyInterface.class.getName());
        config.add(ModelConfiguration.CLASSES_KEY, DummySubClass.class.getName());
        config.add(ModelConfiguration.CLASSES_KEY, DummySuperClas.class.getName());
        config.set(ModelConfiguration.IS_RECURSIVE_KEY, "true");

        // Set up a System Model.
        AbstractUMLEngine engine = UMLEngine.getInstance(config);
        ISystemModel systemModel = engine.createSystemModel();
        systemModel = engine.analyze(systemModel);

        // get classes
        Collection<? extends IClassModel> classes = systemModel.getClasses();
        IClassModel dummyInterface = getClassFromCollection(DummyInterface.class.getName(), classes);
        IClassModel dummyStub = getClassFromCollection(DummySubClass.class.getName(), classes);
        IClassModel dummySuperClass = getClassFromCollection(DummySuperClas.class.getName(), classes);

        // get relations
        Map<ClassPair, List<IRelationInfo>> relations = systemModel.getRelations();
        List<IRelationInfo> relFromStubToSuperClass = relations.get(new ClassPair(dummyStub, dummySuperClass));
        assertEquals(Arrays.asList(new RelationExtendsClass()), relFromStubToSuperClass);

        List<IRelationInfo> relFromStubToInterface = relations.get(new ClassPair(dummyStub, dummyInterface));
        assertNull(relFromStubToInterface);

        List<IRelationInfo> relFromSuperToInterfaceClass = relations
                .get(new ClassPair(dummySuperClass, dummyInterface));
        assertEquals(Arrays.asList(new RelationImplement()), relFromSuperToInterfaceClass);
    }

    @Test
    public void graphVizManyNoFields() {
        String relDummyMany = RelDummyManyClass.class.getName();
        String relOtherDummy = RelOtherDummyClass.class.getName();
        String relDummy = RelDummyClass.class.getName();

        // Set up config.
        Configuration config = Configuration.getInstance();
        config.add(ModelConfiguration.CLASSES_KEY, relDummyMany);
        config.add(ModelConfiguration.CLASSES_KEY, relOtherDummy);
        config.add(ModelConfiguration.CLASSES_KEY, relDummy);
        config.set(ModelConfiguration.IS_RECURSIVE_KEY, "true");
        config.setFilter(data -> data == Modifier.DEFAULT || data == Modifier.PUBLIC);

        // Set up SystemModel and Generator.
        AbstractUMLEngine engine = UMLEngine.getInstance(config);
        ISystemModel systemModel = engine.createSystemModel();
        systemModel = engine.analyze(systemModel);

        // get classes
        Collection<? extends IClassModel> classes = systemModel.getClasses();
        IClassModel RelDummyManyClass = getClassFromCollection(relDummyMany, classes);
        IClassModel RelOtherDummyClass = getClassFromCollection(relOtherDummy, classes);
        IClassModel RelDummyClass = getClassFromCollection(relDummy, classes);

        // get relations.
        Map<ClassPair, List<IRelationInfo>> relations = systemModel.getRelations();

        List<IRelationInfo> relFromManyToOther = relations.get(new ClassPair(RelDummyManyClass, RelOtherDummyClass));
        assertEquals(1, relFromManyToOther.size());
        assertEquals(new RelationDependsOn(true), relFromManyToOther.get(0));

        List<IRelationInfo> relFromOtherToRel = relations.get(new ClassPair(RelDummyManyClass, RelDummyClass));
        assertEquals(1, relFromOtherToRel.size());
        assertEquals(new RelationDependsOn(false), relFromOtherToRel.get(0));

        String actual = engine.generate(systemModel);
        String expectedDependencyCardinality = "\"" + relDummyMany + "\" -> " + "\"" + relOtherDummy
                + "\" [arrowhead=\"vee\" style=\"dashed\" taillabel=\"0..*\" ];";
        assertTrue("Missing GraphViz dependency", actual.contains(expectedDependencyCardinality));
    }

    private IClassModel getClassFromCollection(String name, Collection<? extends IClassModel> classes) {
        for (IClassModel c : classes) {
            if (c.getName().equals(name))
                return c;
        }
        fail("Class " + name + " does not exist");
        return null;
    }

}