package org.memgraphd.data.library;

import java.util.Collections;
import java.util.List;

import org.memgraphd.data.GraphData;
import org.memgraphd.data.comparator.CreatedDateComparator;
import org.memgraphd.data.comparator.IdComparator;
import org.memgraphd.data.comparator.LastModifiedDateComparator;
import org.memgraphd.data.comparator.SortOrder;

public class DefaultSortableDataCollection implements SortableDataCollection {
    
    private final List<GraphData> graphData;
    
    public DefaultSortableDataCollection(List<GraphData> graphData) {
        this.graphData = graphData;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<GraphData> sortById(SortOrder order) {
        Collections.sort(graphData, new IdComparator(order));
        return graphData;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<GraphData> sortByCreatedDate(SortOrder order) {
        Collections.sort(graphData, new CreatedDateComparator(order));
        return graphData;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<GraphData> sortByLastModifiedDate(SortOrder order) {
        Collections.sort(graphData, new LastModifiedDateComparator(order));
        return graphData;
    }

}
