package org.memgraphd.data;
/**
 * This captures all types of data relationships for a {@link GraphData} instance. The two types of
 * relationships are:<br><br>
 * a. Direct links to other data instances.<br>
 * b. References from other data instances this instance.<br><br>
 * 
 * The direct link relationship is basically the {@link Data} instance itself declaring that relationship.<br>
 * The reference relationship is tracked internally by the {@link Graph} for each {@link Data} instance.
 * 
 * @author Ilirjan Papa
 * @since August 28, 2012
 * @see GraphData
 */
public interface GraphRelatedData {
    
    /**
     * Returns all known direct links for this instance of {@link GraphData} encapsulated
     *  in an instance of {@link GraphDataRelationship}.
     * @return {@link GraphDataRelationship}
     */
    GraphDataRelationship getLinks();
    
    /**
     * Returns all known references to this instance of {@link GraphData} by other data instances
     *  encapsulated in an instance of {@link GraphDataRelationship}.
     * @return {@link GraphDataRelationship}
     */
    GraphDataRelationship getReferences();
}
