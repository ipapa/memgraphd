package org.memgraphd.operation;

import org.memgraphd.GraphMappings;
import org.memgraphd.decision.Sequence;
import org.memgraphd.memory.MemoryReference;
import org.memgraphd.memory.operation.MemoryOperations;
/**
 * Default implementation of {@link GraphSeeker}.
 * 
 * @author Ilirjan Papa
 * @since August 2, 2012
 *
 */
public class GraphSeekerImpl extends AbstractGraphAccess implements GraphSeeker {
    private final GraphMappings mappings;
    
    public GraphSeekerImpl(MemoryOperations memoryAccess, GraphMappings mappings) {
        super(memoryAccess);
        this.mappings = mappings;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MemoryReference seekById(String id) {
        return mappings.getById(id);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MemoryReference seekBySequence(Sequence seq) {
        return mappings.getBySequence(seq);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MemoryReference[] seekById(String[] ids) {
        MemoryReference[] refs = new MemoryReference[ids.length];
        for(int i=0; i < ids.length; i++) {
            refs[i] = seekById(ids[i]);
        }
        return refs;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MemoryReference[] seekBySequence(Sequence[] seqs) {
        MemoryReference[] refs = new MemoryReference[seqs.length];
        for(int i=0; i < seqs.length; i++) {
            refs[i] = seekBySequence(seqs[i]);
        }
        return refs;
    }

}
