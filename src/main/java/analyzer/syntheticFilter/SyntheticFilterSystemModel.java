package analyzer.syntheticFilter;

import java.util.ArrayList;
import java.util.Collection;

import analyzer.utility.IClassModel;
import analyzer.utility.ISystemModel;
import analyzer.utility.ISystemModelFilter;

public class SyntheticFilterSystemModel extends ISystemModelFilter {

    SyntheticFilterSystemModel(ISystemModel systemModel) {
        super(systemModel);
    }

    @Override
    public Collection<? extends IClassModel> getClasses() {
        Collection<IClassModel> classes = new ArrayList<>();
        for (IClassModel clazz : super.getClasses()) {
            if (!clazz.isSynthetic())
                classes.add(new SyntheticFilterClassModel(clazz));
        }
        return classes;
    }

}
