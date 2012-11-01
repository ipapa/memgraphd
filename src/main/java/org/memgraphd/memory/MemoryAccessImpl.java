package org.memgraphd.memory;

import java.util.LinkedList;
import java.util.Set;

import org.memgraphd.data.GraphData;
import org.memgraphd.data.GraphDataRelationshipImpl;
import org.memgraphd.data.GraphRelatedDataImpl;
import org.memgraphd.memory.operation.MemoryLocationOperations;


public final class MemoryAccessImpl implements MemoryAccess {
    private final MemoryManager memoryManager;
    
    private static final GraphData[] EMPTY = new GraphData[0];
    
    public MemoryAccessImpl(MemoryManager manager) {
        this.memoryManager = manager;
    }
    
    private MemoryLocation getMemoryLocation(MemoryReference ref) {
        
        if(ref.id() < 0 || ref.id() >= memoryManager.capacity())
            throw new IllegalStateException("Reference id out of range: " + ref.id());
        
        return memoryManager.read(ref);
    }
    
    @Override
    public MemoryReference write(GraphData item) {
        MemoryBlock block = memoryManager.resolver().resolve(item.getDecision().getRequest());
        MemoryReference nextAvailableRef = block.next();
        ((MemoryLocationOperations)memoryManager.read(nextAvailableRef)).update(item);
        return nextAvailableRef;
    }

    @Override
    public GraphData read(MemoryReference ref) {
        return getMemoryLocation(ref).data();
    }
    
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
    
    @Override
    public void update(MemoryReference ref, GraphData data) {
        ((MemoryLocationOperations)getMemoryLocation(ref)).update(data);
    }

    @Override
    public void link(MemoryReference ref, MemoryReference[] links) {
        linkDelink(ref, links, true);
    }

    @Override
    public void delink(MemoryReference ref, MemoryReference[] links) {
        linkDelink(ref, links, false);
    }

    private void linkDelink(MemoryReference ref, MemoryReference[] links, boolean isLink) {
        for(MemoryReference link : links) {
            if(link != null) {
               linkDelink(ref, link, isLink);
            }
        }
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
    
    @Override
    public void link(MemoryReference ref, MemoryReference link) {
        linkDelink(ref, link, true);
    }

    @Override
    public void delink(MemoryReference ref, MemoryReference link) {
        linkDelink(ref, link, false);
    }

    @Override
    public void free(MemoryReference ref) {
        MemoryLocation location = getMemoryLocation(ref);
        if(location != null) {
            ((MemoryLocationOperations)location).free();
        }
    } 
    
    private GraphData read(MemoryLocation location, boolean isLink, LinkedList<GraphData> graph) {
        GraphData[] links = EMPTY;
        GraphData[] references = EMPTY;
        if(isLink && !location.getLinks().isEmpty()) {
            links = getRelatedData(location.getLinks(), isLink, graph);
            ((GraphRelatedDataImpl)location.data().getRelatedData()).
                setRelationships(new GraphDataRelationshipImpl(links));
        }
        else if(!isLink && !location.getReferences().isEmpty()) {
            references = getRelatedData(location.getReferences(), isLink, graph);
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

    @Override
    public void delinkAll(MemoryReference ref) {
        MemoryLocation loc = getMemoryLocation(ref);
        for(MemoryLocation ml : loc.getLinks()) {
            ((MemoryLocationOperations)loc).delink(ml);
            ((MemoryLocationOperations)ml).dereference(loc);
        }
    }

    @Override
    public void dereferenceAll(MemoryReference ref) {
        MemoryLocation loc = getMemoryLocation(ref);
        for(MemoryLocation ml : loc.getReferences()) {
            ((MemoryLocationOperations)loc).dereference(ml);
            ((MemoryLocationOperations)ml).delink(loc);
        }
    }
    
}
