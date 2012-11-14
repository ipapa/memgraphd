package org.memgraphd.memory;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.memgraphd.data.GraphData;
import org.memgraphd.memory.operation.MemoryBlockOperations;
import org.memgraphd.memory.operation.MemoryLocationOperations;

/**
 * Implements {@link MemoryLocation} and {@link MemoryLocationOperations}.
 * 
 * @author Ilirjan Papa
 * @since July 31, 2012
 *
 */
public class MemoryLocationImpl implements MemoryLocation, MemoryLocationOperations {
    private final MemoryReference reference;
    private final Set<MemoryLocation> links;
    private final Set<MemoryLocation> references;
    private GraphData data;
    private MemoryBlock block;
    
    public MemoryLocationImpl(MemoryReference ref, GraphData data) {
        this.reference = ref;
        this.data = data;
        this.links = new CopyOnWriteArraySet<MemoryLocation>();
        this.references = new CopyOnWriteArraySet<MemoryLocation>();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final MemoryReference reference() {
        return reference;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final GraphData data() {
        return data;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final MemoryBlock block() {
        return this.block;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void reserve(MemoryBlock block) {
        if(block == null)
            throw new IllegalArgumentException("No Reservation - Memory block is null");
        setBlock(block);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final synchronized void update(GraphData data) {
        this.data = data;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final synchronized void link(MemoryLocation location) {
        if(!links.contains(location)) {
            links.add(location);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final synchronized void delink(MemoryLocation location) {
        links.remove(location);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final synchronized void reference(MemoryLocation ref) {
        if(!references.contains(ref)) {
            references.add(ref);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final synchronized void dereference(MemoryLocation loc) {
        references.remove(loc);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final synchronized void free() {
        data = null;
        ((MemoryBlockOperations)block).recycle(reference());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final Set<MemoryLocation> links() {
        return links;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final Set<MemoryLocation> references() {
        return references;
    }
    
    private final synchronized void setBlock(MemoryBlock block) {
        this.block = block;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void delinkAll() {
        for(MemoryLocation ml : links()) {
            delink(ml);
            ((MemoryLocationOperations)ml).dereference(this);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void dereferenceAll() {
        for(MemoryLocation ml : references()) {
            dereference(ml);
            ((MemoryLocationOperations)ml).delink(this);
        }
    }
}
