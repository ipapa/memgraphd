package org.memgraphd.data;

import java.util.LinkedHashSet;
import java.util.Set;

import org.memgraphd.Graph;
/**
 * It represents a set of relationships for a {@link GraphData}. Allows you to filter these relationships
 * by relationship type and class type.
 * 
 * @author Ilirjan Papa
 * @since August 28, 2012
 * @see GraphRelatedData
 */
public interface GraphDataRelationship {
    
    /**
     * Returns all data relationships available.
     * @return array of {@link GraphData}
     */
    GraphData[] relationships();
    
    /**
     * Find the first occurrence of this className and return it.
     * @param className {@link Class}
     * @return T
     */
    <T> T oneToOne(Class<T> className);
    
    /**
     * Find all data relationships of this className and return them in a Set.
     * The set will also preserve the order of these relationships as they were in the {@link Graph}.
     * @param className {@link Class}
     * @return {@link LinkedHashSet} of T
     */
    <T> Set<T> oneToMany(Class<T> className);

}
