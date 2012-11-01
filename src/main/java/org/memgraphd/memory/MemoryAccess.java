package org.memgraphd.memory;

import org.memgraphd.memory.operation.MemoryFree;
import org.memgraphd.memory.operation.MemoryLink;
import org.memgraphd.memory.operation.MemoryRead;
import org.memgraphd.memory.operation.MemoryUpdate;
import org.memgraphd.memory.operation.MemoryWrite;


public interface MemoryAccess extends MemoryRead, MemoryWrite, MemoryUpdate, MemoryFree, MemoryLink {
    
}
