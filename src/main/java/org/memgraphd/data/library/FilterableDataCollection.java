package org.memgraphd.data.library;

import org.memgraphd.data.GraphData;

/**
 * Given a set of {@link GraphData}, you can filter it down to what you want.
 * 
 * @author Ilirjan Papa
 * @since November 20, 2012
 */
public interface FilterableDataCollection {
    
    /**
     * Filter by nothing - in other words - do not filter, return the data set as-is.
     * @return {@link SortableDataCollection}
     */
    SortableDataCollection filterBy();
    
    /**
     * Filter by a data category.
     * @param category {@link Category}
     * @return {@link SortableDataCollection}
     */
    SortableDataCollection filterBy(Category category);
    
    /**
     * Filter by a list of data categories. It will return all items in this collection 
     * that belong to <b>ALL</b> categories provided.
     * @param categories array of {@link Category}
     * @return {@link SortableDataCollection}
     */
    SortableDataCollection filterByAll(Category[] categories);
    
    /**
     * Filter by a list of data categories. It will return all items in this collection
     *  that belong to <b>ANY</b> categories provided.
     * @param categories array of {@link Category}
     * @return {@link SortableDataCollection}
     */
    SortableDataCollection filterByAny(Category[] categories);
    
    /**
     * Filter by using the {@link DataPredicate} provided. 
     * @param predicate {@link DataPredicate}
     * @return {@link SortableDataCollection}
     */
    SortableDataCollection filterBy(DataPredicate predicate);
}
