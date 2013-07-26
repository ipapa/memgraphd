package org.memgraphd.data.library;

import org.memgraphd.data.Data;
import org.memgraphd.data.GraphData;
/**
 * A simple {@link Data} predicate interface.
 * 
 * @author Ilirjan Papa
 * @since November 20, 2012
 *
 */
public interface DataPredicate {
    
    boolean apply(final GraphData data);
    
}
