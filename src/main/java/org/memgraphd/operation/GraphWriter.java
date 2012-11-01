package org.memgraphd.operation;

import org.memgraphd.data.GraphData;
import org.memgraphd.exception.GraphException;
import org.memgraphd.memory.MemoryReference;

public interface GraphWriter {
    
    MemoryReference write(GraphData data) throws GraphException;
    
    MemoryReference[] write(GraphData[] data) throws GraphException;
 
    void delete(GraphData data) throws GraphException;
    
    void delete(GraphData[] data) throws GraphException;
    
}
