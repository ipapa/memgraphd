package org.memgraphd.data;

import org.memgraphd.decision.Decision;
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
     * Returns the decision information about this data.
     * @return {@link}
     */
    Decision getDecision();
    
    /**
     * 
     * @return {@link Data}
     */
    Data getData();
    
    GraphRelatedData getRelatedData();
    
}
