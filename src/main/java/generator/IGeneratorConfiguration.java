package generator;

import utility.IFilter;
import utility.Modifier;

/**
 * An Interface for Generator Configuration.
 * <p>
 * Created by lamd on 12/12/2016.
 * Edited by fineral on 12/13/2016.
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
    IFilter<Modifier> getModifierFilters();

    /**
     * Returns either BT or TB depending on how you want the
     * UML to show.
     *
     * @return BT or TB
     */
    String getRankDir();

    /**
     * Returns the Graph styling for every Node.
     *
     * @return Node styling for entire graph.
     */
    String getNodeStyle();

    /**
     * Returns the parser key for generator to use.
     *
     * @return Parser key.
     */
    String getParseKey();
}
