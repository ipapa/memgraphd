package org.memgraphd.data.library;

import org.memgraphd.data.GraphData;

/**
 * A {@link Category} is a subset of a {@link LibrarySection}.
 * 
 * @author Ilirjan Papa
 * @since November 20, 2012
 *
 */
public interface Category {
    
    /**
     * Returns the name of the Category, has to be unique in the context of {@link LibrarySection}.
     * @return {@link String}
     */
    String getName();
    
    /**
     * Returns the DataPredicate that determines if {@link GraphData} qualifies to be part of this category or not.
     * @return {@link DataPredicate}
     */
    DataPredicate getPredicate();
    
}
