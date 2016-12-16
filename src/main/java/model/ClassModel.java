package model;

import analyzer.IVisitable;
import analyzer.IVisitor;
import generator.IClassModel;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import utility.ClassType;
import utility.Modifier;

import java.util.*;

/**
 * Representing classes in java program
 * <p>
 * This class handles inheritance, dependency relationship among classes.
 * <p>
 * <p>
 * A class may have four different type of methods -- constructor, instance
 * method, static method, static initializer This model has four separate
 * getters for each of them
 *
 * @author zhang
 */
class ClassModel implements IVisitable<ClassModel>, ASMServiceProvider, IClassModel {
    private final ASMServiceProvider asmServiceProvider;
    private final ClassNode asmClassNode;

    private final Modifier modifier;
    private final boolean isFinal;
    private final ClassType classType;
    private final String name;

    private ClassModel superClass;
    private Collection<ClassModel> interfaces;

    private Map<String, FieldModel> fields;
    private Map<Signature, MethodModel> methods;

    /**
     * Creates an ClassModel and assign its basic properties.
     *
     * @param asmServiceProvider
     * @param asmClassNode
     * @param important
     */
    public ClassModel(ASMServiceProvider asmServiceProvider, ClassNode asmClassNode) {
        this.asmServiceProvider = asmServiceProvider;
        this.asmClassNode = asmClassNode;
        this.modifier = Modifier.parse(asmClassNode.access);
        this.isFinal = Modifier.parseIsFinal(asmClassNode.access);
        this.classType = ClassType.parse(asmClassNode.access);
        this.name = Type.getObjectType(asmClassNode.name).getClassName();
    }

    public IClassModel getSuperClass() {
        if (superClass == null && asmClassNode.superName != null)
            superClass = getClassByName(asmClassNode.superName);
        return superClass;
    }

    public String getName() {
        return name;
    }

    public ClassType getType() {
        return classType;
    }

    public Modifier getModifier() {
        return modifier;
    }

    public Iterable<ClassModel> getInterfaces() {
        if (interfaces == null) {
            interfaces = new ArrayList<>();
            @SuppressWarnings("unchecked")
            List<String> ls = asmClassNode.interfaces;
            for (String s : ls) {
                ClassModel m = getClassByName(s);
                if (m != null)
                    interfaces.add(m);
            }
        }
        return interfaces;
    }

    @Override
    public List<IClassModel> getHasRelation() {
        return new ArrayList<>();
    }

    @Override
    public List<IClassModel> getDependsRelation() {
        return new ArrayList<>();
    }

    @Override
    public String getSuperClassName() {
        return this.superClass.getName();
    }

    public Iterable<MethodModel> getMethods() {
        return getMethodsMap().values();
    }

    public MethodModel getMethodBySignature(Signature signature) {
        if (methods.containsKey(signature))
            return methods.get(signature);
        return null;
    }

    private Map<Signature, MethodModel> getMethodsMap() {
        if (methods == null) {
            methods = new HashMap<>();

            @SuppressWarnings("unchecked")
            List<MethodNode> ls = asmClassNode.methods;
            for (MethodNode methodNode : ls) {
                MethodModel methodModel = new MethodModel(this, methodNode);
                Signature signature = methodModel.getSignature();
                methods.put(signature, methodModel);
            }
        }
        return methods;
    }

    public Iterable<FieldModel> getFields() {
        return getFieldMap().values();
    }

    private Map<String, FieldModel> getFieldMap() {
        if (fields == null) {
            fields = new HashMap<>();
            @SuppressWarnings("unchecked")
            List<FieldNode> ls = asmClassNode.fields;
            for (FieldNode fieldNode : ls) {
                FieldModel fieldModel = new FieldModel(this, fieldNode);
                fields.put(fieldModel.getName(), fieldModel);
            }
        }
        return fields;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public ClassModel getClassByName(String name) {
        return asmServiceProvider.getClassByName(name);
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public void visit(IVisitor<ClassModel> IVisitor) {
        IVisitor.visit(this);
    }


}