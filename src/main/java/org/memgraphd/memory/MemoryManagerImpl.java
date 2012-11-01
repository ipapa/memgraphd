package org.memgraphd.memory;

import org.memgraphd.memory.operation.MemoryBlockResolver;
import org.memgraphd.memory.operation.MemoryLocationOperations;

public final class MemoryManagerImpl implements MemoryManager {
    private final MemoryLocation[] buffer;
    private final MemoryBlockResolver requestResolver;
    
    public MemoryManagerImpl(MemoryBlockResolver resolver) {
        this.requestResolver = resolver;
        this.buffer = new MemoryLocation[capacity()];
        
        initialize();
        
        reserveBlocks(resolver.blocks());
    }

    @Override
    public final int capacity() {
        int capacity = 0;
        for(MemoryBlock mb : blocks()) {
            capacity += mb.capacity();
        }
        return capacity;
    }
    
    @Override
    public int occupied() {
        int occupied = 0;
        for(MemoryBlock mb : blocks()) {
            occupied += mb.occupied();
        }
        return occupied;
    }

    @Override
    public int available() {
        int available = 0;
        for(MemoryBlock mb : blocks()) {
            available += mb.available();
        }
        return available;
    }
    
    @Override
    public int recycled() {
        int recycled = 0;
        for(MemoryBlock mb : blocks()) {
            recycled += mb.recycled();
        }
        return recycled;
    }
    
    @Override
    public final MemoryBlock[] blocks() {
        return resolver().blocks();
    }

    @Override
    public final MemoryBlockResolver resolver() {
        return requestResolver;
    }
    
    @Override
    public final MemoryLocation read(MemoryReference ref) {
        if(ref.id() < 0 || ref.id() >= capacity()) {
            throw new IllegalArgumentException("Out of bound memory reference: " + ref.id());
        }
        return buffer[ref.id()];
    }
    
    private void initialize() {
        for(int i=0; i < capacity(); i++) {
            buffer[i] = new MemoryLocationImpl(MemoryReference.valueOf(i), null);
        }
    }
    
    private void reserveBlocks(MemoryBlock[] blocks) {
        int startWith = 0;
        for(MemoryBlock block : blocks) {
            ((MemoryBlockImpl) block).setBoundaries(MemoryReference.valueOf(startWith), MemoryReference.valueOf(startWith + (block.capacity() -1)));
            for(int i = startWith; i < block.capacity(); i++) {
                if(startWith > capacity()) {
                    throw new IllegalArgumentException("MemoryBlock sizes do not match size of memory allocated");
                }
                ((MemoryLocationOperations)buffer[i]).reserve(block);
                startWith++;
            }
        }
    }

}
