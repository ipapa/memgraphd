package org.memgraphd.memory.operation;

import org.memgraphd.memory.MemoryReference;

public interface MemoryBlockOperations {
    
    void setBoundaries(MemoryReference startsWith, MemoryReference endsWith);
    
    void recycle(MemoryReference reference);
    
}
