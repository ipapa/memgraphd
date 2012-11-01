package org.memgraphd.memory;



public interface MemoryBlock extends MemoryStats {
    
    String name();
    
    MemoryReference startsWith();
    
    MemoryReference endsWith();
    
    MemoryReference next();
    
}
