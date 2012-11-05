package org.memgraphd.operation;

import org.memgraphd.Graph;
import org.memgraphd.data.GraphData;
import org.memgraphd.decision.Decision;
import org.memgraphd.exception.GraphException;
import org.memgraphd.memory.MemoryReference;
/**
 * Responsible for handling all write and delete events in the {@link Graph}.
 * 
 * @author Ilirjan Papa
 * @since August 4, 2012
 *
 */
public interface GraphWriter {
    
    /**
     * Write {@link Decision} into the {@link Graph} and return the {@link MemoryReference} assigned
     * to this data instance for future references by the caller.
     * 
     * @param decision {@link GraphData}
     * @return {@link MemoryReference}
     * @throws GraphException
     */
    MemoryReference write(Decision decision) throws GraphException;
    
    /**
     * Same functionality as {@link #write(Decision)} but this time we are writing more than one decision instances
     * into the graph and for each write event we are returning its assigned memory reference.
     * @param data array of {@link Decision}
     * @return array of {@link MemoryReference}
     * @throws GraphException
     */
    MemoryReference[] write(Decision[] decisions) throws GraphException;
    
    /**
     * Delete this {@link GraphData} from the {@link Graph}.
     * @param data {@link GraphData}
     * @throws GraphException
     */
    void delete(GraphData data) throws GraphException;
    
    /**
     * Delete multiple {@link GraphData} from the {@link Graph}.
     * @param data array of {@link GraphData}
     * @throws GraphException
     */
    void delete(GraphData[] data) throws GraphException;
    
}
