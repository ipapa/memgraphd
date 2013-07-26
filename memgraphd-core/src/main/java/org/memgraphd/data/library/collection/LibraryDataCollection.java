package org.memgraphd.data.library.collection;

import org.memgraphd.data.GraphData;
import org.memgraphd.data.library.Library;

/**
 * Represents a {@link Library} data collection that can be sorted and filtered.
 * @author Ilirjan Papa
 * @since January 2, 2013
 *
 */
public interface LibraryDataCollection extends SortableDataCollection, FilterableDataCollection {
    
    /**
     * Returns the size of the collection.
     * @return integer
     */
    int size();
    
    /**
     * Returns true if gData is contained in this collection, false otherwise.
     * @param gData {@link GraphData}
     * @return boolean true or false
     */
    boolean contains(GraphData gData);
    
    /**
     * Returns a list of GraphData objects in this collection.
     * @return array of {@link GraphData}
     */
    GraphData[] list();
}
