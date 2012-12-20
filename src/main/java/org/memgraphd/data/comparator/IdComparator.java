package org.memgraphd.data.comparator;

import java.text.Collator;
import java.util.Locale;

import org.memgraphd.data.Data;
import org.memgraphd.data.GraphData;
/**
 * Sort graph data collection by {@link Data#getId()}.
 * 
 * @author Ilirjan Papa
 * @since December 19, 2012
 *
 */
public class IdComparator extends GraphDataComparator {
    
    private final Collator collator = Collator.getInstance(Locale.US);
    
    public IdComparator(SortOrder order) {
        super(order);
    }

    @Override
    public int compare(GraphData g1, GraphData g2) {
        return getOrder().order() * collator.compare(g1.getData().getId(), g2.getData().getId());
    }

}
