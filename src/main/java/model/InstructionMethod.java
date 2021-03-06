package model;

import org.objectweb.asm.tree.MethodInsnNode;

import analyzer.utility.IClassModel;

import java.util.Collection;
import java.util.HashSet;

/**
 * represent a java byte code instruction that calls a method
 *
 * @author zhang
 */
public class InstructionMethod extends InstructionModel {
    private final TypeModel calledOn;
    private final MethodModel method;

    public InstructionMethod(MethodModel belongTo, MethodInsnNode methodCall) {
        super(belongTo);
        this.calledOn = TypeParser.parseClassInternalName(methodCall.owner);
        ClassModel destClass = calledOn.getClassModel();
        if (destClass == null) {
            this.method = null;
        } else {
            Signature signature = Signature.parse(methodCall.name, methodCall.desc);
            method = destClass.getMethodBySignature(signature);
            if (belongTo == null) {
                System.err.println(getClass().getName() + "Method is null \tdesc\t" + methodCall.desc + "\tname\t"
                        + methodCall.name + "\towner\t" + methodCall.owner);
            }
        }
    }

    @Override
    public Collection<TypeModel> getDependentTypes() {
        Collection<TypeModel> depends = new HashSet<>();
        depends.add(calledOn);
        if (method != null) {
            depends.addAll(method.getArguments());
            depends.add(method.getReturnType());
        }
        return depends;
    }

    /**
     * @return the method this instruction calls
     */
    public MethodModel getAccessComponent() {
        return this.method;
    }
    
    @Override
    public IClassModel getCalledOn() {
        return calledOn.getClassModel();
    }

}
