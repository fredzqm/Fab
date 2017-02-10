package analyzer.decorator;

import analyzer.utility.IClassModel;
import analyzer.utility.IFieldModel;
import analyzer.utility.IMethodModel;
import analyzer.utility.ISystemModel;
import utility.MethodType;

/**
 * A Decorator Abstract class that contains basic utility methods used by both
 * Good and Bad Decorator Analyzers.
 * <p>
 * Created by lamd on 2/7/2017.
 */
public abstract class DecoratorTemplate extends AdapterDecoratorTemplate {
    protected boolean hasParentAsField(IClassModel child, IClassModel parent) {
        return child.getFields().stream().anyMatch((field) -> field.getFieldType().equals(parent));
    }

    protected boolean hasParentAsConstructorArgument(IClassModel child, IClassModel parent) {
        return child.getMethods().stream().filter((method) -> method.getMethodType() == MethodType.CONSTRUCTOR)
                .flatMap((method) -> method.getArguments().stream()).anyMatch(parent::equals);
    }

    protected boolean isParentFieldCalled(IClassModel parent, IMethodModel method) {
        return method.getAccessedFields().stream().map(IFieldModel::getFieldType)
                .anyMatch((type) -> type.equals(parent));
    }

    @Override
    protected void updateRelatedClasses(ISystemModel systemModel, IClassModel decoratorClass, IFieldModel field,
            IClassModel parent) {
        systemModel.getClasses().stream().filter((classModel) -> decoratorClass.equals(classModel.getSuperClass()))
                .forEach((classModel) -> {
                    addCommonFillColor(systemModel, classModel);
                    systemModel.addClassModelSteretypes(classModel, this.config.getRelatedClassStereotype());
                });
    }
}
