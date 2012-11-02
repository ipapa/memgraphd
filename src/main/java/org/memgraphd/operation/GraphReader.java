package org.memgraphd.operation;

import org.memgraphd.data.Data;
import org.memgraphd.data.GraphData;
import org.memgraphd.decision.Sequence;
import org.memgraphd.memory.MemoryReference;

import com.sun.corba.se.impl.orbutil.graph.Graph;
/**
 * Responsible for granting read-access-only to {@link GraphData} in our {@link Graph}.
 * 
 * @author Ilirjan Papa
 * @since August 1, 2012
 *
 */
public interface GraphReader {
    
    /**
     * Read {@link GraphData} by {@link Data#getId()}.
     * @param id {@link String}
     * @return {@link GraphData}
     */
    GraphData readId(String id);
    
    /**
     * Read {@link GraphData} by {@link Sequence}.
     * @param seq {@link Sequence}
     * @return {@link GraphData}
     */
    GraphData readSequence(Sequence seq);
    
    /**
     * Read {@link GraphData} by memory reference.
     * @param ref {@link MemoryReference}
     * @return {@link GraphData}
     */
    GraphData readReference(MemoryReference ref);
    
    /**
     * Same functionality as {@link #readId(String)} but for more than one id(s).
     * @param ids array of {@link String}
     * @return {@link GraphData}
     */
    GraphData[] readIds(String[] ids);
    
    /**
     * Same functionality as {@link #readSequence(Sequence)} but for more than one sequence(s).
     * @param seqs array of {@link Sequence}
     * @return {@link GraphData}
     */
    GraphData[] readSequences(Sequence[] seqs);
    
    /**
     * Same functionality oas {@link #readReference(MemoryReference)} but for more than one memory reference(s).
     * @param refs array of {@link MemoryReference}
     * @return {@link GraphData}
     */
    GraphData[] readReferences(MemoryReference[] refs);
    
}
