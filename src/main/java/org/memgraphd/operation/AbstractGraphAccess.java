package org.memgraphd.operation;

import org.memgraphd.memory.MemoryAccess;

public abstract class AbstractGraphAccess {
    private final MemoryAccess memoryAccess;
    
    public AbstractGraphAccess(MemoryAccess memoryAccess) {
        this.memoryAccess = memoryAccess;
    }
    
    protected MemoryAccess getMemoryAccess() {
        return this.memoryAccess;
    }
}
