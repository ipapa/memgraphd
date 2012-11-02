package org.memgraphd.operation;

import org.memgraphd.data.GraphData;
import org.memgraphd.decision.Sequence;
import org.memgraphd.memory.MemoryBlock;
import org.memgraphd.memory.MemoryReference;
import org.memgraphd.memory.operation.MemoryOperations;
/**
 * A base implementation for {@link GraphFilter}.
 * 
 * @author Ilirjan Papa
 * @since October 15, 2012
 *
 */
public class GraphFilterImpl extends AbstractGraphAccess implements GraphFilter {
    //TODO Show me some love and implement me
    public GraphFilterImpl(MemoryOperations memoryAccess) {
        super(memoryAccess);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GraphData[] filterBy(MemoryBlock block) {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GraphData[] filterByRange(MemoryReference startRef, MemoryReference endRef) {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GraphData[] filterByRange(Sequence startSeq, Sequence endSeq) {
        // TODO Auto-generated method stub
        return null;
    }

}
