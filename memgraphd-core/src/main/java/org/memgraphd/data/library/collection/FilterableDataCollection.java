package org.memgraphd.data.library.collection;

import org.memgraphd.data.GraphData;
import org.memgraphd.data.library.Category;
import org.memgraphd.data.library.DataPredicate;

/**
 * Given a set of {@link GraphData}, you can filter it down to what you want.
 * 
 * @author Ilirjan Papa
 * @since November 20, 2012
 */
public interface FilterableDataCollection {
    
    /**
     * Filter by nothing - in other words - do not filter, return the data set as-is.
     * @return {@link LibraryDataCollection}
     */
    LibraryDataCollection filterBy();
    
    /**
     * Filter by a data category.
     * @param category {@link Category}
     * @return {@link LibraryDataCollection}
     */
    LibraryDataCollection filterBy(Category category);
    
    /**
     * Filter by a list of data categories. It will return all items in this collection 
     * that belong to <b>ALL</b> categories provided.
     * @param categories array of {@link Category}
     * @return {@link LibraryDataCollection}
     */
    LibraryDataCollection filterByAll(Category[] categories);
    
    /**
     * Filter by a list of data categories. It will return all items in this collection
     *  that belong to <b>ANY</b> categories provided.
     * @param categories array of {@link Category}
     * @return {@link LibraryDataCollection}
     */
    LibraryDataCollection filterByAny(Category[] categories);
    
    /**
     * Filter by using the {@link DataPredicate} provided. 
     * @param predicate {@link DataPredicate}
     * @return {@link LibraryDataCollection}
     */
    LibraryDataCollection filterBy(DataPredicate predicate);
}
