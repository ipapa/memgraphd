package org.memgraphd.memory.operation;

import org.memgraphd.data.GraphData;
import org.memgraphd.memory.MemoryReference;


public interface MemoryRead {
    
    GraphData read(MemoryReference reference);
    
    GraphData readGraph(MemoryReference reference);
   
}
