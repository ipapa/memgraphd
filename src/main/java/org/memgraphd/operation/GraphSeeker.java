package org.memgraphd.operation;

import org.memgraphd.decision.Sequence;
import org.memgraphd.memory.MemoryReference;

public interface GraphSeeker {
    
    MemoryReference seekById(String id);
    
    MemoryReference seekBySequence(Sequence seq);
    
    MemoryReference[] seekById(String[] ids);
    
    MemoryReference[] seekBySequence(Sequence[] seqs);
}
