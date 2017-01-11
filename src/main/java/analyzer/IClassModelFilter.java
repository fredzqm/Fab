package analyzer;

import java.util.Collection;

import utility.ClassType;

/**
 * a filter for IClassModel
 *
 * @author zhang
 */
public class IClassModelFilter implements IClassModel {
    private IClassModel classModel;

    /**
     * Constructs a Class Model Filter
     *
     * @param classModel classModel decorated.
     */
    public IClassModelFilter(IClassModel classModel) {
        this.classModel = classModel;
    }

    protected IClassModel getClassModel() {
        return classModel;
    }

    public String getName() {
        return classModel.getName();
    }

    public ClassType getType() {
        return classModel.getType();
    }

    public boolean isFinal() {
        return classModel.isFinal();
    }

    public boolean isStatic() {
        return classModel.isStatic();
    }

    public IClassModel getSuperClass() {
        return classModel.getSuperClass();
    }

    public Collection<? extends IClassModel> getInterfaces() {
        return classModel.getInterfaces();
    }

    public Collection<? extends IFieldModel> getFields() {
        return classModel.getFields();
    }

    public Collection<? extends IMethodModel> getMethods() {
        return classModel.getMethods();
    }

    public Collection<String> getStereoTypes() {
        return classModel.getStereoTypes();
    }

    public String getLabel() {
        return classModel.getLabel();
    }

}
