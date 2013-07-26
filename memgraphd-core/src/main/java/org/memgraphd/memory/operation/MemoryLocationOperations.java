package org.memgraphd.memory.operation;

import org.memgraphd.data.GraphData;
import org.memgraphd.memory.MemoryBlock;
import org.memgraphd.memory.MemoryLocation;

public interface MemoryLocationOperations {
    
    void reserve(MemoryBlock block);
    
    void update(GraphData data);
    
    void free();
    
    void link(MemoryLocation location);
    
    void delink(MemoryLocation location);
    
    void reference(MemoryLocation ref);
    
    void dereference(MemoryLocation ref);
    
    void delinkAll();
    
    void dereferenceAll();

}
