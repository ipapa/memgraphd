package org.memgraphd.data.comparator;

import org.memgraphd.data.Data;
import org.memgraphd.data.GraphData;
/**
 * Sort graph data collection by {@link Data#getCreatedDate()}.
 * 
 * @author Ilirjan Papa
 * @since December 19, 2012
 *
 */
public class CreatedDateComparator extends GraphDataComparator {

    public CreatedDateComparator(SortOrder order) {
        super(order);
    }

    @Override
    public int compare(GraphData g1, GraphData g2) {
        return getOrder().order() * 
                g1.getData().getCreatedDate().compareTo(g2.getData().getCreatedDate());
    }

}
