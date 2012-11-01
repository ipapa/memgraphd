package org.memgraphd.memory.operation;

import org.memgraphd.data.GraphData;
import org.memgraphd.memory.MemoryLocation;
import org.memgraphd.memory.MemoryStats;

public interface MemoryLocationOperations {
    
    void reserve(MemoryStats block);
    
    void update(GraphData data);
    
    void free();
    
    void link(MemoryLocation location);
    
    void delink(MemoryLocation location);
    
    void reference(MemoryLocation ref);
    
    void dereference(MemoryLocation ref);

}
