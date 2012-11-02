package org.memgraphd.memory;

import org.memgraphd.data.Data;
import org.memgraphd.request.GraphRequest;
/**
 * It is responsible for managing memory blocks and resolving a {@link GraphRequest} to a {@link MemoryBlock}.
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
     * Given a {@link GraphRequest}, it will inspect the request to determine
     * the memory block where the data is store or should be stored. The idea here 
     * is that {@link Data} of the same type will be stored contiguously in a dedicated {@link MemoryBlock}.
     * @param request {@link GraphRequest}
     * @return {@link MemoryBlock}
     */
    MemoryBlock resolve(GraphRequest request);
    
}
