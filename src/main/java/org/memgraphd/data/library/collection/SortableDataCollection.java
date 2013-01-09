package org.memgraphd.data.library.collection;

import org.memgraphd.data.comparator.SortOrder;

public interface SortableDataCollection {
    
    LibraryDataCollection sortById(SortOrder order);
    
    LibraryDataCollection sortByCreatedDate(SortOrder order);
    
    LibraryDataCollection sortByLastModifiedDate(SortOrder order);
}
