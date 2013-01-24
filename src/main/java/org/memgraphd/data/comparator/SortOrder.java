package org.memgraphd.data.comparator;


/**
 * Display sort order.
 * 
 * @author Ilirjan Papa
 * @since December 19, 2012
 *
 */
public enum SortOrder {
    ASCENDING(1),
    DESCENDING(-1);
    
    private final int order;
    
    private SortOrder(int order) {
        this.order = order;
    }
    
    public final int order() {
        return this.order;
    }
}
