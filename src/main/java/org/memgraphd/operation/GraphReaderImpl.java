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
    public final GraphData read(String id) {
        MemoryReference ref = seeker.seekById(id);
        if(ref != null) {
            return read(ref);
        }
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final GraphData read(Sequence seq) {
        MemoryReference ref = seeker.seekBySequence(seq);
        if(ref != null) {
            return read(ref);
        }
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GraphData read(MemoryReference ref) {
        return getMemoryAccess().readGraph(ref);
    }
    
}
