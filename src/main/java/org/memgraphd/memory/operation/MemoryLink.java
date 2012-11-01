package org.memgraphd.memory.operation;

import org.memgraphd.memory.MemoryReference;

public interface MemoryLink {
    
    void link(MemoryReference ref, MemoryReference link);
    
    void delink(MemoryReference ref, MemoryReference link);
    
    void link(MemoryReference ref, MemoryReference[] links);
    
    void delink(MemoryReference ref, MemoryReference[] links);
    
    void delinkAll(MemoryReference ref);
    
    void dereferenceAll(MemoryReference ref);
}
