package org.memgraphd.operation;

import org.memgraphd.data.GraphData;
import org.memgraphd.decision.Sequence;
import org.memgraphd.memory.MemoryAccess;
import org.memgraphd.memory.MemoryReference;

public class GraphReaderImpl extends AbstractGraphAccess implements GraphReader {
    private final GraphSeeker seeker;
    
    public GraphReaderImpl(MemoryAccess memoryAccess, GraphSeeker seeker) {
        super(memoryAccess);
        this.seeker = seeker;
    }

    @Override
    public final GraphData readId(String id) {
        MemoryReference ref = seeker.seekById(id);
        return ref != null ? getMemoryAccess().readGraph(ref) : null;
    }
    
    @Override
    public final GraphData readSequence(Sequence seq) {
        MemoryReference ref = seeker.seekBySequence(seq);
        return ref != null ? getMemoryAccess().read(ref) : null;
    }

    @Override
    public final GraphData[] readIds(String[] ids) {
        GraphData[] result = new GraphData[ids.length];
        for(int i=0; i < ids.length; i++) {
            result[i] = readId(ids[i]);
        }
        return result;
    }

    @Override
    public final GraphData[] readSequences(Sequence[] seqs) {
        GraphData[] result = new GraphData[seqs.length];
        for(int i=0; i < seqs.length; i++) {
            result[i] = readSequence(seqs[i]);
        }
        return result;
    }

    @Override
    public GraphData readReference(MemoryReference ref) {
        return getMemoryAccess().read(ref);
    }

    @Override
    public GraphData[] readReferences(MemoryReference[] refs) {
        GraphData[] result = new GraphData[refs.length];
        for(int i=0; i < refs.length; i++) {
            result[i] = readReference(refs[i]);
        }
        return result;
    }

}
