package org.memgraphd.memory.operation;

import org.memgraphd.data.GraphData;
import org.memgraphd.memory.MemoryReference;

public interface MemoryUpdate {
    
    void update(MemoryReference ref, GraphData data);
    
}
