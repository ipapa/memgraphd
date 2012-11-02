package org.memgraphd.memory;

import org.memgraphd.data.Data;


/**
 * You can partition the memory buffer into memory blocks, assigned to storing only
 * a certain type of {@link Data}. So that data does not get fragmented in the buffer.
 * 
 * @author Ilirjan Papa
 * @since July 28, 2012
 *
 */
public interface MemoryBlock extends MemoryStats {
    /**
     * The name of the memory block in question.
     * @return {@link String}
     */
    String name();
    
    /**
     * Returns the memory reference where the block starts in the memory buffer.
     * @return {@link MemoryReference}
     */
    MemoryReference startsWith();
    
    /**
     * Returns the memory reference where the block ends in the memory buffer.
     * @return {@link MemoryReference}
     */
    MemoryReference endsWith();
    
    /**
     * Returns the next available {@link MemoryReference} in the block.
     * @return
     */
    MemoryReference next();
    
}
