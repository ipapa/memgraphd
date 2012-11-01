package org.memgraphd.memory;

public interface MemoryStats {
    
    int capacity();
    
    int occupied();
    
    int available();
    
    int recycled();
    
}