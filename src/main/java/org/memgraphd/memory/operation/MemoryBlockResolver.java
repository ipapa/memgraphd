package org.memgraphd.memory.operation;

import org.memgraphd.memory.MemoryBlock;
import org.memgraphd.request.GraphRequest;

public interface MemoryBlockResolver {
    
    MemoryBlock[] blocks();
    
    MemoryBlock resolve(GraphRequest request);
    
}
