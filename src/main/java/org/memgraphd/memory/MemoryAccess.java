package org.memgraphd.memory;

import java.util.LinkedList;
import java.util.Set;

import org.memgraphd.data.GraphData;
import org.memgraphd.data.GraphDataRelationshipImpl;
import org.memgraphd.data.GraphRelatedDataImpl;
import org.memgraphd.memory.operation.MemoryLocationOperations;
import org.memgraphd.memory.operation.MemoryOperations;

/**
 * Default implementation of {@link MemoryAccess} that gives you full read-write access to memory layer.
 * 
 * @author Ilirjan Papa
 * @since July 17, 2012
 *
 */
public final class MemoryAccess implements MemoryOperations {
    private final MemoryManager memoryManager;
    
    private static final GraphData[] EMPTY = new GraphData[0];
    
    public MemoryAccess(MemoryManager manager) {
        this.memoryManager = manager;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MemoryReference write(GraphData item) {
        MemoryBlock block = memoryManager.resolver().resolve(item.getDecision().getRequest());
        MemoryReference nextAvailableRef = block.next();
        ((MemoryLocationOperations)memoryManager.read(nextAvailableRef)).update(item);
        return nextAvailableRef;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GraphData read(MemoryReference ref) {
        return getMemoryLocation(ref).data();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GraphData readGraph(MemoryReference reference) {
        MemoryLocation location = getMemoryLocation(reference);
        LinkedList<GraphData> graph = new LinkedList<GraphData>();
        read(location, true, graph);
        GraphData last = graph.peekFirst();
        if(last != null) {
            read(getMemoryLocation(last.getReference()), false, graph);
        }
        return read(location, false, graph);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void update(MemoryReference ref, GraphData data) {
        ((MemoryLocationOperations)getMemoryLocation(ref)).update(data);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void link(MemoryReference ref, MemoryReference[] links) {
        linkDelink(ref, links, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delink(MemoryReference ref, MemoryReference[] links) {
        linkDelink(ref, links, false);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void link(MemoryReference ref, MemoryReference link) {
        linkDelink(ref, link, true);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void delink(MemoryReference ref, MemoryReference link) {
        linkDelink(ref, link, false);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void free(MemoryReference ref) {
        MemoryLocation location = getMemoryLocation(ref);
        if(location != null) {
            ((MemoryLocationOperations)location).free();
        }
    } 
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void delinkAll(MemoryReference ref) {
        MemoryLocation loc = getMemoryLocation(ref);
        for(MemoryLocation ml : loc.links()) {
            ((MemoryLocationOperations)loc).delink(ml);
            ((MemoryLocationOperations)ml).dereference(loc);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void dereferenceAll(MemoryReference ref) {
        MemoryLocation loc = getMemoryLocation(ref);
        for(MemoryLocation ml : loc.references()) {
            ((MemoryLocationOperations)loc).dereference(ml);
            ((MemoryLocationOperations)ml).delink(loc);
        }
    }

    private void linkDelink(MemoryReference ref, MemoryReference[] links, boolean isLink) {
        for(MemoryReference link : links) {
            if(link != null) {
               linkDelink(ref, link, isLink);
            }
        }
    }

    private MemoryLocation getMemoryLocation(MemoryReference ref) {
        
        if(ref.id() < 0 || ref.id() >= memoryManager.capacity())
            throw new IllegalStateException("Reference id out of range: " + ref.id());
        
        return memoryManager.read(ref);
    }

    private GraphData read(MemoryLocation location, boolean isLink, LinkedList<GraphData> graph) {
        GraphData[] links = EMPTY;
        GraphData[] references = EMPTY;
        if(isLink && !location.links().isEmpty()) {
            links = getRelatedData(location.links(), isLink, graph);
            ((GraphRelatedDataImpl)location.data().getRelatedData()).
                setRelationships(new GraphDataRelationshipImpl(links));
        }
        else if(!isLink && !location.references().isEmpty()) {
            references = getRelatedData(location.references(), isLink, graph);
            ((GraphRelatedDataImpl)location.data().getRelatedData()).
                setReferences(new GraphDataRelationshipImpl(references));
        }
        return location.data();
    }

    private GraphData[] getRelatedData(Set<MemoryLocation> relatedLocs, boolean isLink, LinkedList<GraphData> graph) {
        GraphData[] relationships = new GraphData[relatedLocs.size()];
        int count = 0;
        for(MemoryLocation loc : relatedLocs) {
            GraphData data = read(loc, isLink, graph);
            graph.add(data);
            relationships[count] =  data;
            count++;
        }
        return relationships;
    }

    private void linkDelink(MemoryReference ref, MemoryReference link, boolean isLink) {
        MemoryLocation loc = getMemoryLocation(ref);
        if(loc != null) {
            MemoryLocation linkLoc = getMemoryLocation(link);
            if(linkLoc != null && isLink) {
                ((MemoryLocationOperations)loc).link(linkLoc);
                ((MemoryLocationOperations)linkLoc).reference(loc);
            }
            else if(linkLoc != null && !isLink) {
                ((MemoryLocationOperations)loc).delink(linkLoc);
                ((MemoryLocationOperations)linkLoc).delink(loc);
            }
        }
    }
    
}
