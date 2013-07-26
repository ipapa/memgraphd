package org.memgraphd.memory.operation;

import org.memgraphd.memory.MemoryBlock;
import org.memgraphd.memory.MemoryReference;
/**
 * Operations that can be performed on a {@link MemoryBlock}.
 * 
 * @author Ilirjan Papa
 * @since July 28, 2012
 *
 */
public interface MemoryBlockOperations {
    
    /**
     * Reserve your block of memory locations in the memory buffer. 
     * @param startsWith {@link MemoryReference} where the block starts
     * @param endsWith {@link MemoryReference} where the block ends
     */
    void setBoundaries(MemoryReference startsWith, MemoryReference endsWith);
    
    /**
     * You can recycle a memory reference if it is no longer in-use.
     * @param reference {@link MemoryReference}
     */
    void recycle(MemoryReference reference);
    
}
