package generator;

import java.util.Collection;
import java.util.Set;

/**
 * An Interface for Generator Configuration.
 * <p>
 * Created by lamd on 12/12/2016.
 */
public interface IGeneratorConfiguration {

    /**
     * Returns the Node Seperation.
     *
     * @return Node Separation value.
     */
    double getNodeSep();

    /**
     * Return the set of Method Access Filters.
     *
     * @return Set of Method Access Filters.
     */
    Collection<IModifier> getFilters();
}