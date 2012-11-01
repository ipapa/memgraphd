package org.memgraphd.operation;

import org.memgraphd.data.GraphData;
import org.memgraphd.decision.Sequence;
import org.memgraphd.memory.MemoryReference;
import org.memgraphd.memory.MemoryStats;


public interface GraphFilter {
    
    GraphData[] filterBy(MemoryStats block);
    
    GraphData[] filterByRange(MemoryReference startRef, MemoryReference endRef);
    
    GraphData[] filterByRange(Sequence startSeq, Sequence endSeq);
}
