package org.memgraphd.memory;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.memgraphd.data.GraphData;
import org.memgraphd.memory.operation.MemoryLocationOperations;


public class MemoryLocationImpl implements MemoryLocation, MemoryLocationOperations {
    private final MemoryReference reference;
    private final Set<MemoryLocation> links;
    private final Set<MemoryLocation> references;
    private GraphData data;
    private MemoryStats block;
    
    public MemoryLocationImpl(MemoryReference ref, GraphData data) {
        this.reference = ref;
        this.data = data;
        this.links = new CopyOnWriteArraySet<MemoryLocation>();
        this.references = new CopyOnWriteArraySet<MemoryLocation>();
    }
    
    @Override
    public final MemoryReference reference() {
        return reference;
    }

    @Override
    public final GraphData data() {
        return data;
    }
    
    @Override
    public final MemoryStats block() {
        return this.block;
    }

    @Override
    public final void reserve(MemoryStats block) {
        if(block == null)
            throw new IllegalArgumentException("No Reservation - Memory block is null");
        setBlock(block);
    }

    @Override
    public final synchronized void update(GraphData data) {
        this.data = data;
    }
    
    @Override
    public final void link(MemoryLocation location) {
        if(!links.contains(location)) {
            links.add(location);
        }
    }
    
    @Override
    public final void delink(MemoryLocation location) {
        links.remove(location);
    }
    
    @Override
    public final void reference(MemoryLocation ref) {
        if(!references.contains(ref)) {
            references.add(ref);
        }
    }

    @Override
    public final void dereference(MemoryLocation loc) {
        references.remove(loc);
    }
    
    @Override
    public final synchronized void free() {
        data = null;
        ((MemoryBlockImpl)block).recycle(reference());
    }
    
    private final synchronized void setBlock(MemoryStats block) {
        this.block = block;
    }

    @Override
    public final Set<MemoryLocation> getLinks() {
        return links;
    }

    @Override
    public final Set<MemoryLocation> getReferences() {
        return references;
    }

}
