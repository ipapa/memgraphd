package org.memgraphd.memory;

import org.memgraphd.data.Data;
/**
 * It is responsible for managing memory blocks and resolving a write/update requests to a {@link MemoryBlock}.
 * 
 * @author Ilirjan Papa
 * @since July 19, 2012
 *
 */
public interface MemoryBlockResolver {
    /**
     * Returns all memory blocks in-use.
     * @return array of {@link MemoryBlock}
     */
    MemoryBlock[] blocks();
    
    /**
     * Given a {@link Data}, it will inspect the object to determine
     * the memory block where it should be stored. The idea here 
     * is that {@link Data} of the same type will be stored contiguously in a dedicated {@link MemoryBlock}.
     * @param data {@link Data}
     * @return {@link MemoryBlock}
     */
    MemoryBlock resolve(Data data);
    
}
