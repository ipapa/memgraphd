package org.memgraphd.data;

import java.util.Set;
/**
 * It represents a set of relationships for a {@link GraphData}. Allows you to filter these relationships
 * by relationship type and class type.
 * 
 * @author Ilirjan Papa
 * @since August 28, 2012
 * @see GraphRelatedData
 */
public interface GraphDataRelationship {
    
    GraphData[] relationships();
    
    <T> T oneToOne(Class<T> className);
    
    <T> Set<T> oneToMany(Class<T> className);

}
