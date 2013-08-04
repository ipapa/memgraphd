package org.memgraphd.memory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implements the {@link MemoryBlock} and all its operations.
 *
 * @author Ilirjan Papa
 * @since July 28, 2012
 *
 */
public final class MemoryBlockImpl implements MemoryBlock {
    private final String name;
    private final AtomicInteger capacity;
    private final Queue<MemoryReference> recycled;

    private AtomicInteger cursor;
    private MemoryReference startsWith;
    private MemoryReference endsWith;

    public MemoryBlockImpl(String name, MemoryReference start, MemoryReference end) {
        this.name = name;
        this.capacity = new AtomicInteger(end.id() - start.id());
        this.recycled = new ConcurrentLinkedQueue<MemoryReference>();
        this.cursor = new AtomicInteger(start.id() - 1);

        validate(start, end);

        this.startsWith = start;
        this.endsWith = end;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String name() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int capacity() {
        return capacity.intValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int occupied() {
        return cursor.intValue() - startsWith().id() - recycled.size() + 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int available() {
        return capacity.intValue() - (cursor.intValue() + 1) + recycled.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int recycled() {
        return recycled.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final MemoryReference startsWith() {
        return startsWith;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final MemoryReference endsWith() {
        return endsWith;
    }

    private final void validate(MemoryReference startsWith, MemoryReference endsWith) {
        if(startsWith.id() > endsWith.id()) {
            throw new IllegalArgumentException("Invalid memory block range for memory block.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final MemoryReference next() {
        if(cursor.intValue() == endsWith().id()) {
            throw new IllegalStateException("Memory block " + name() + " is full.");
        }
        MemoryReference ref = recycled.poll();
        return ref != null ? ref : MemoryReference.valueOf(cursor.incrementAndGet());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void recycle(MemoryReference reference) {
        recycled.add(reference);
    }

}
