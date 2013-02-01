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
    public GraphData readReference(MemoryReference ref) {
        return readReference(ref, false);
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

}
