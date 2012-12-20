package org.memgraphd.data.library;

import java.util.List;

import org.memgraphd.data.GraphData;
import org.memgraphd.data.comparator.SortOrder;

public interface SortableDataCollection {
    
    List<GraphData> sortById(SortOrder order);
    
    List<GraphData> sortByCreatedDate(SortOrder order);
    
    List<GraphData> sortByLastModifiedDate(SortOrder order);
}
