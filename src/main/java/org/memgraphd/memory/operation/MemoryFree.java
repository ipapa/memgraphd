package org.memgraphd.memory.operation;

import org.memgraphd.memory.MemoryReference;

public interface MemoryFree {
    
    void free(MemoryReference ref);
    
}
