package org.memgraphd.memory.operation;

import org.memgraphd.data.GraphData;
import org.memgraphd.memory.MemoryReference;


public interface MemoryWrite {
    
    MemoryReference write(GraphData item);

}
