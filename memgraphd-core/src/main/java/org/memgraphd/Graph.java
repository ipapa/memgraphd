package org.memgraphd;

import org.memgraphd.data.GraphData;
import org.memgraphd.data.library.Library;
import org.memgraphd.operation.GraphFilter;
import org.memgraphd.operation.GraphReader;
import org.memgraphd.operation.GraphWriter;
/**
 * Represents the actual data graph with all its available operations. Thing to note here
 * is that the Graph implementation can be casted as a GraphReader for read-only access,
 * or GraphWriter for write-only access. This way you have a clean separation of concerns
 * between the thread in charge of write requests and the one that might be handling read requests.
 * 
 * @author Ilirjan Papa
 * @since August 17, 2012
 *
 */
public interface Graph extends GraphReader, GraphWriter, GraphFilter, GraphSupervisor {
    
    /**
     * The name given to this {@link Graph} instance.
     * @return String
     */
    String getName();
    
    /**
     * Returns the data {@link Library} instance clients can use to filter/group/sort
     * large collections of {@link GraphData}.
     * @return {@link Library}
     */
    Library getLibrary();
    
}
