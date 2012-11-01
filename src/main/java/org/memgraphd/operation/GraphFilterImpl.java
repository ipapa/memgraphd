package org.memgraphd.operation;

import org.memgraphd.data.GraphData;
import org.memgraphd.decision.Sequence;
import org.memgraphd.memory.MemoryAccess;
import org.memgraphd.memory.MemoryReference;
import org.memgraphd.memory.MemoryStats;

public class GraphFilterImpl extends AbstractGraphAccess implements GraphFilter {

    public GraphFilterImpl(MemoryAccess memoryAccess) {
        super(memoryAccess);
    }

    @Override
    public GraphData[] filterBy(MemoryStats block) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public GraphData[] filterByRange(MemoryReference startRef, MemoryReference endRef) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public GraphData[] filterByRange(Sequence startSeq, Sequence endSeq) {
        // TODO Auto-generated method stub
        return null;
    }

}
