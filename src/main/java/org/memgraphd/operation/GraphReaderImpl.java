package org.memgraphd.operation;

import org.memgraphd.data.GraphData;
import org.memgraphd.decision.Sequence;
import org.memgraphd.memory.MemoryReference;
import org.memgraphd.memory.operation.MemoryOperations;
/**
 * Base implementation of {@link GraphReader}.
 * 
 * @author Ilirjan Papa
 * @since August 1, 2012
 *
 */
public class GraphReaderImpl extends AbstractGraphAccess implements GraphReader {
    private final GraphSeeker seeker;
    
    public GraphReaderImpl(MemoryOperations memoryAccess, GraphSeeker seeker) {
        super(memoryAccess);
        this.seeker = seeker;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final GraphData readId(String id) {
        return readId(id, false);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final GraphData readSequence(Sequence seq) {
        return readSequence(seq, false);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final GraphData[] readIds(String[] ids) {
        return readIds(ids, false);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final GraphData[] readSequences(Sequence[] seqs) {
        return readSequences(seqs, false);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GraphData readReference(MemoryReference ref) {
        return readReference(ref, false);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GraphData[] readReferences(MemoryReference[] refs) {
        return readReferences(refs, false);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final GraphData readGraph(String id) {
        return readId(id, true);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final GraphData readGraph(Sequence seq) {
        return readSequence(seq, true);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final GraphData readGraph(MemoryReference ref) {
        return readReference(ref, true);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final GraphData[] readGraph(String[] ids) {
        return readIds(ids, true);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final GraphData[] readGraph(Sequence[] seqs) {
        return readSequences(seqs, true);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final GraphData[] readGraph(MemoryReference[] refs) {
        return readReferences(refs, true);
    }

    private GraphData[] readSequences(Sequence[] seqs, boolean includeRelationships) {
        GraphData[] result = new GraphData[seqs.length];
        for(int i=0; i < seqs.length; i++) {
            if(includeRelationships) {
                result[i] = readGraph(seqs[i]);
            }
            else {
                result[i] = readSequence(seqs[i]);
            }
        }
        return result;
    }

    private GraphData readReference(MemoryReference ref, boolean includeRelationships) {
        if(includeRelationships) {
            return getMemoryAccess().readGraph(ref);
        }
        return getMemoryAccess().read(ref);
    }

    private GraphData readSequence(Sequence seq, boolean includeRelationships) {
        MemoryReference ref = seeker.seekBySequence(seq);
        if(includeRelationships) {
            return ref != null ? getMemoryAccess().readGraph(ref) : null;
        }
        return ref != null ? getMemoryAccess().read(ref) : null;
    }

    private GraphData readId(String id, boolean includeRelationships) {
        MemoryReference ref = seeker.seekById(id);
        if(includeRelationships) {
            return ref != null ? getMemoryAccess().readGraph(ref) : null;
        }
        return ref != null ? getMemoryAccess().read(ref) : null;
    }

    private GraphData[] readIds(String[] ids, boolean includeRelationships) {
        GraphData[] result = new GraphData[ids.length];
        for(int i=0; i < ids.length; i++) {
            if(includeRelationships) {
                result[i] = readGraph(ids[i]);
            }
            else {
                result[i] = readId(ids[i]);
            }
        }
        return result;
    }

    private GraphData[] readReferences(MemoryReference[] refs, boolean includeRelationships) {
        GraphData[] result = new GraphData[refs.length];
        for(int i=0; i < refs.length; i++) {
            if(includeRelationships) {
                result[i] = readGraph(refs[i]);
            }
            else {
                result[i] = readReference(refs[i]);
            }
        }
        return result;
    }

}
