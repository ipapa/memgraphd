package org.memgraphd.memory;

import java.util.Set;

import org.memgraphd.data.GraphData;


public interface MemoryLocation {
    
    MemoryReference reference();
    
    GraphData data();

    MemoryStats block();
    
    Set<MemoryLocation> getLinks();
    
    Set<MemoryLocation> getReferences();
    
}
