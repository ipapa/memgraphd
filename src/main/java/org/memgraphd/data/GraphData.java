package org.memgraphd.data;

import org.memgraphd.Graph;
import org.memgraphd.decision.Sequence;
import org.memgraphd.memory.MemoryReference;

/**
 * Serves as a wrapper around {@link Data} to incorporate all other pieces of information that we
 * know about this data internally.
 * 
 * @author Ilirjan Papa
 * @since September 4, 2012
 *
 */
public interface GraphData {
    
    /**
     * Returns the memory reference where this instance is stored.
     * @return {@link MemoryReference}
     */
    MemoryReference getReference();
    
    /**
     * Returns the decision sequence assigned to this instance of data.
     * @return {@link Sequence}
     */
    Sequence getSequence();
    
    /**
     * The actual data that was written in the {@link Graph}.
     * @return {@link Data}
     */
    Data getData();
    
    /**
     * Related data to this {@link Data}; direct links or references.
     * @return {@link GraphRelatedData}
     */
    GraphRelatedData getRelatedData();
    
}
