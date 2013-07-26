package org.memgraphd.memory;

/**
 * It is responsible for managing the memory buffer. Its responsibilities consist of:<br>
 * a. Partition the memory buffer into memory blocks.<br>
 * b. Resolve an incoming request for graph data to a partition.<br>
 * c. Read a memory reference and grant access to its {@link MemoryLocation}.<br>
 * 
 * @author Ilirjan Papa
 * @since July 31, 2012
 *
 */
public interface MemoryManager extends MemoryStats {
    
    /**
     * Returns a list of all memory blocks currently in-use.
     * @return an array of {@link MemoryBlock}
     */
    MemoryBlock[] blocks();
    
    /**
     * Returns the {@link MemoryBlockResolver} instance that is being used to resolve requests.
     * @return {@link MemoryBlockResolver}
     */
    MemoryBlockResolver resolver();
    
    /**
     * Gain access to the {@link MemoryLocation} with this {@link MemoryReference}.
     * @param ref {@link MemoryReference}
     * @return {@link MemoryLocation}
     */
    MemoryLocation read(MemoryReference ref);

}
