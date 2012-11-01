package org.memgraphd.data.relationship;

import org.memgraphd.data.Data;

/**
 * {@link Data} objects should implement this interface if with have relationships.
 * 
 * @author Ilirjan Papa
 * @since June 23, 2012
 *
 */
public interface DataRelationship {
    /**
     * Unique identifier of the data in question.
     * @return {@link String}
     */
    String getId();
    
    /**
     * A list of data id(s) of all my known associations/relationships.
     * @return array of {@link String}
     */
    String[] getRelatedIds();
    
}
