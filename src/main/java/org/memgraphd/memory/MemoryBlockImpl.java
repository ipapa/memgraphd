package org.memgraphd.memory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.memgraphd.memory.operation.MemoryBlockOperations;


public final class MemoryBlockImpl implements MemoryBlock, MemoryBlockOperations {
    private final String name;
    private final AtomicInteger capacity;
    private final Queue<MemoryReference> recycled;
    
    private AtomicInteger cursor;
    private MemoryReference startsWith;
    private MemoryReference endsWith;
    
    public MemoryBlockImpl(String name, int size) {
        if(size < 1) {
            throw new IllegalArgumentException("Size of memory block should be greater than zero");
        }
        this.name = name;
        this.capacity = new AtomicInteger(size);
        this.recycled = new ConcurrentLinkedQueue<MemoryReference>();
        setCursor(0);
    }
    
    @Override
    public final String name() {
        return name;
    }

    @Override
    public final int capacity() {
        return capacity.intValue();
    }

    @Override
    public final int occupied() {
        return cursor.intValue() - startsWith().id() + recycled.size() + 1;
    }
    
    @Override
    public final int available() {
        return capacity.intValue() - (cursor.intValue() + 1) + recycled.size();
    }
    
    @Override
    public int recycled() {
        return recycled.size();
    }
    
    @Override
    public final MemoryReference startsWith() {
        return startsWith;
    }

    @Override
    public final MemoryReference endsWith() {
        return endsWith;
    }

    @Override
    public final synchronized void setBoundaries(MemoryReference startsWith, MemoryReference endsWith) {
        if(startsWith() != null || endsWith() != null) {
            throw new IllegalStateException("Block boundaries already set.");
        }
        if(startsWith == null || endsWith == null) {
            throw new IllegalArgumentException("Invalid memory block range for memory block.");
        }
        if((endsWith.id() - startsWith.id()) != capacity() - 1) {
            throw new IllegalArgumentException("Range does not equal memory block size.");
        }
        setCursor(startsWith.id() - 1);
        this.startsWith = startsWith;
        this.endsWith = endsWith;
    }
    
    private void setCursor(int cursor) {
        this.cursor = new AtomicInteger(cursor);
    }

    @Override
    public final MemoryReference next() {
        if(cursor.intValue() == endsWith().id()) {
            throw new IllegalStateException("Memory block " + name() + " is full.");
        }
        MemoryReference ref = recycled.poll();
        return ref != null ? ref : MemoryReference.valueOf(cursor.incrementAndGet());
    }

    @Override
    public final void recycle(MemoryReference reference) {
        recycled.add(reference);
    }

}
