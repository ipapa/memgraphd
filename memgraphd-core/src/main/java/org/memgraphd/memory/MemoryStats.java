package org.memgraphd.memory;

import org.memgraphd.data.GraphData;

/**
 * An interface that offers a set of metric used by {@link MemoryBlock} and {@link MemoryManager}.
 * 
 * @author Ilirjan Papa
 * @since October 27, 2012
 *
 */
public interface MemoryStats {
    /**
     * Total capacity in terms of {@link MemoryLocation}(s) and {@link GraphData} objects we can store.
     * @return integer
     */
    int capacity();
    
    /**
     * How many {@link MemoryLocation}(s) have been currently occupied by {@link GraphData}.
     * @return integer
     */
    int occupied();
    
    /**
     * How many {@link MemoryLocation}(s) are free and available for use.
     * @return integer
     */
    int available();
    
    /**
     * How many {@link MemoryLocation}(s) have been recycled and waiting to be reused.
     * @return integer
     */
    int recycled();
    
}