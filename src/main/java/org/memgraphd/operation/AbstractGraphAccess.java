package org.memgraphd.operation;

import org.memgraphd.memory.operation.MemoryOperations;
/**
 * A layer of abstraction for all beans that require access to {@link MemoryOperations}.
 * @author Ilirjan Papa
 * @since August 27, 2012
 *
 */
public abstract class AbstractGraphAccess {
    private final MemoryOperations memoryAccess;
    
    public AbstractGraphAccess(MemoryOperations memoryAccess) {
        this.memoryAccess = memoryAccess;
    }
    /**
     * Grants accest o {@link MemoryOperations} instance.
     * @return {@link MemoryOperations}
     */
    protected MemoryOperations getMemoryAccess() {
        return this.memoryAccess;
    }
}
