package org.memgraphd.memory;

import org.memgraphd.memory.operation.MemoryBlockResolver;

public interface MemoryManager extends MemoryStats {
    
    MemoryBlock[] blocks();
    
    MemoryBlockResolver resolver();
    
    MemoryLocation read(MemoryReference ref);

}
