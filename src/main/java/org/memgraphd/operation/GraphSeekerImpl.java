package org.memgraphd.operation;

import org.memgraphd.GraphMappings;
import org.memgraphd.decision.Sequence;
import org.memgraphd.memory.MemoryAccess;
import org.memgraphd.memory.MemoryReference;

public class GraphSeekerImpl extends AbstractGraphAccess implements GraphSeeker {
    private final GraphMappings mappings;
    
    public GraphSeekerImpl(MemoryAccess memoryAccess, GraphMappings mappings) {
        super(memoryAccess);
        this.mappings = mappings;
    }
    
    @Override
    public MemoryReference seekById(String id) {
        return mappings.getById(id);
    }

    @Override
    public MemoryReference seekBySequence(Sequence seq) {
        return mappings.getBySequence(seq);
    }

    @Override
    public MemoryReference[] seekById(String[] ids) {
        MemoryReference[] refs = new MemoryReference[ids.length];
        for(int i=0; i < ids.length; i++) {
            refs[i] = seekById(ids[i]);
        }
        return refs;
    }

    @Override
    public MemoryReference[] seekBySequence(Sequence[] seqs) {
        MemoryReference[] refs = new MemoryReference[seqs.length];
        for(int i=0; i < seqs.length; i++) {
            refs[i] = seekBySequence(seqs[i]);
        }
        return refs;
    }

}
