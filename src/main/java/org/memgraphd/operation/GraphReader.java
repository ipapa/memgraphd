package org.memgraphd.operation;

import org.memgraphd.data.GraphData;
import org.memgraphd.decision.Sequence;
import org.memgraphd.memory.MemoryReference;

public interface GraphReader {
    
    GraphData readId(String id);
    
    GraphData readSequence(Sequence seq);
    
    GraphData readReference(MemoryReference ref);
    
    GraphData[] readIds(String[] ids);
    
    GraphData[] readSequences(Sequence[] seqs);
    
    GraphData[] readReferences(MemoryReference[] refs);
    
}
