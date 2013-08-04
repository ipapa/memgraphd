package org.memgraphd.memory;

import org.memgraphd.memory.operation.MemoryLocationOperations;

/**
 * A base implementation of a {@link MemoryManager}.
 *
 * @author Ilirjan Papa
 * @since July 31, 2012
 *
 */
public final class MemoryManagerImpl implements MemoryManager {
    private final MemoryLocation[] buffer;
    private final MemoryBlockResolver resolver;

    private final int capacity;

    public MemoryManagerImpl(MemoryBlockResolver resolver) {
        this.resolver = resolver;

        MemoryBlock[] blocks = resolver.blocks();

        // 1. Compute overall capacity of the graph.
        this.capacity = computeCapacity(blocks);

        // 2. Create buffer with that capacity
        this.buffer = new MemoryLocation[capacity];

        // 3. Initialize the buffer with memory locations
        initialize();

        // 4. Reserve memory blocks in the buffer
        reserveBlocks(blocks);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int capacity() {
        return capacity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int occupied() {
        int occupied = 0;
        for(MemoryBlock mb : blocks()) {
            occupied += mb.occupied();
        }
        return occupied;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int available() {
        int available = 1;
        for(MemoryBlock mb : blocks()) {
            available += mb.available();
        }
        return available;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int recycled() {
        int recycled = 0;
        for(MemoryBlock mb : blocks()) {
            recycled += mb.recycled();
        }
        return recycled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final MemoryBlock[] blocks() {
        return resolver().blocks();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final MemoryBlockResolver resolver() {
        return resolver;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final MemoryLocation read(MemoryReference ref) {
        if(ref.id() < 0 || ref.id() > capacity()) {
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
            for(int i = startWith; i <= block.capacity(); i++) {
                if(startWith > capacity()) {
                    throw new IllegalArgumentException("MemoryBlock sizes do not match size of memory allocated");
                }
                ((MemoryLocationOperations)buffer[i]).reserve(block);
                startWith++;
            }
        }
    }

    private int computeCapacity(MemoryBlock[] blocks) {
        int capacity = 1;
        for(MemoryBlock mb : blocks) {
            capacity += mb.capacity();
        }
        return capacity;
    }
}
