package org.memgraphd.operation;

import org.memgraphd.data.Data;
import org.memgraphd.decision.Sequence;
import org.memgraphd.memory.MemoryReference;

/**
 * Responsible for translating data id(s) and sequence number(s) into memory reference(s).
 * @author Ilirjan Papa
 * @since August 2, 2012
 *
 */
public interface GraphSeeker {
    /**
     * Returns the memory reference for this {@link Data#getId()}.
     * @param id {@link String}
     * @return {@link MemoryReference} or null if not found.
     */
    MemoryReference seekById(String id);
    
    /**
     * Returns the memory reference for this {@link Sequence}.
     * @param seq {@link Sequence}
     * @return {@link MemoryReference}
     */
    MemoryReference seekBySequence(Sequence seq);
    
    /**
     * Same functionality as {@link #seekById(String)} but for more than one id(s).
     * @param ids array of {@link String}
     * @return array of {@link MemoryReference}
     */
    MemoryReference[] seekById(String[] ids);
    
    /**
     * Same functionality as {@link #seekBySequence(Sequence)} but for more than one sequence(s).
     * @param seqs array of {@link Sequence}
     * @return array of {@link MemoryReference}
     */
    MemoryReference[] seekBySequence(Sequence[] seqs);
}
