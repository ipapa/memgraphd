package org.memgraphd.data.comparator;

import java.util.Comparator;

import org.memgraphd.data.GraphData;

/**
 * Abstract comparator class to be subclassed by all other graph data comparators.
 * 
 * @author Ilirjan Papa
 * @since December 19, 2012
 *
 */
public abstract class GraphDataComparator implements Comparator<GraphData> {
    
    private final SortOrder order;
    
    public GraphDataComparator(SortOrder order) {
        this.order = order;
    }
    
    protected SortOrder getOrder() {
        return order;
    }
    
}
