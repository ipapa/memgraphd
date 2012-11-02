package org.memgraphd.operation;

import org.memgraphd.Graph;
import org.memgraphd.data.GraphData;
import org.memgraphd.decision.Sequence;
import org.memgraphd.memory.MemoryBlock;
import org.memgraphd.memory.MemoryReference;

/**
 * Provides filter capabilities in the {@link Graph}.
 * 
 * @author Ilirjan Papa
 * @since October 15, 2012 
 *
 */
public interface GraphFilter {
    
    /**
     * Filter by {@link MemoryBlock}, read only parts of the buffer you care about.
     * 
     * @param block {@link MemoryBlock}
     * @return array of {@link GraphData}
     */
    GraphData[] filterBy(MemoryBlock block);
    
    /**
     * Filter by range of memory references. Read all the data store in between these memory references.
     * 
     * @param startRef {@link MemoryReference}
     * @param endRef {@link MemoryReference}
     * @return
     */
    GraphData[] filterByRange(MemoryReference startRef, MemoryReference endRef);
    
    /**
     * Filter by range of sequence numbers. In case you want to get a range of decision sequences
     * and their corresponding {@link GraphData} in the {@link Graph}.
     * @param startSeq {@link Sequence}
     * @param endSeq {@link Sequence}
     * @return array of {@link GraphData}
     */
    GraphData[] filterByRange(Sequence startSeq, Sequence endSeq);
}
