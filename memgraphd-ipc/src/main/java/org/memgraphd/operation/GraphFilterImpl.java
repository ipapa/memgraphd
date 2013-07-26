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
    private final GraphReader reader;
    
    public GraphFilterImpl(MemoryOperations memoryAccess, GraphReader reader) {
        super(memoryAccess);
        this.reader = reader;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GraphData[] filterBy(MemoryBlock block) {
        return filterByRange(block.startsWith(), block.endsWith());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GraphData[] filterByRange(MemoryReference startRef, MemoryReference endRef) {
        MemoryReference[] refs = MemoryReference.rangeOf(startRef.id(), endRef.id());
        GraphData[] result = new GraphData[refs.length];
        int count = 0;
        for(MemoryReference ref : refs) {
            result[count] = reader.read(ref);
            count++;
        }
        return result;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GraphData[] filterByRange(Sequence startSeq, Sequence endSeq) {
        Sequence[] range = Sequence.rangeOf(startSeq.number(), endSeq.number());
        GraphData[] result = new GraphData[range.length];
        int count = 0;
        for(Sequence seq : range) {
            result[count] = reader.read(seq);
            count++;
        }
        return result;
    }

}
