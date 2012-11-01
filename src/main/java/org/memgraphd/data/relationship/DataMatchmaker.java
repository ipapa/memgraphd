package org.memgraphd.data.relationship;

import org.memgraphd.data.Data;

/**
 * Acts as a data matchmakers, hooking data instances up with each-other.
 * 
 * @author Ilirjan Papa
 * @since July 23, 2012
 *
 */
public interface DataMatchmaker {
    
    /**
     * Handles a {@link Data} instance that is not in an relationship.
     * @param data {@link Data}
     */
    void bachelor(Data data);
    
    /**
     * Matches a {@link Data} instance that wants to be in a relationship.
     * @param data {@link DataRelationship}
     */
    void match(DataRelationship data);
    
    /**
     * Handles legal issues when data instances hop from one relationship to another.
     * @param oldVows {@link DataRelationship}
     * @param newVows {@link DataRelationship}
     */
    void revow(DataRelationship oldVows, DataRelationship newVows);
    
    /**
     * Handles ugly break-ups when a relationship dies.
     * @param data {@link DataRelationship}
     */
    void separate(DataRelationship data);
}
