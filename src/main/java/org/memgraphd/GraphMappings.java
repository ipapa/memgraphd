package org.memgraphd;

import org.memgraphd.decision.Sequence;
import org.memgraphd.memory.MemoryReference;
/**
 * Keeps track of and manages all Graph mappings:<br>
 * 
 * data id -> memory reference<br>
 * sequence -> memory reference<br>
 * 
 * @author Ilirjan Papa
 * @since July 27, 2012
 */
public interface GraphMappings {
    
    /**
     * Checks to see if this data id already has a mapping.
     * @param id data id as {@link String}
     * @return true if it is there, false otherwise.
     */
    boolean containsId(String id);
    
    /**
     * Translate a data id into a {@link MemoryReference}.
     * @param id data id as {@link String}
     * @return {@link MemoryReference}
     */
    MemoryReference getById(String id);
    
    /**
     * Store a new mapping data id -> memory reference.
     * @param id data id as {@link String}
     * @param ref {@link MemoryReference}
     */
    void put(String id, MemoryReference ref);
    
    /**
     * Delete the mapping for this data id.
     * @param id data id as {@link String}
     */
    void delete(String id);
    
    /**
     * Delete the mapping for this sequence.
     * @param seq {@link Sequence}
     */
    void delete(Sequence seq);
    
    /**
     * Check to see if this sequence already has a mapping.
     * @param sequence {@link Sequence}
     * @return
     */
    boolean containsSequence(Sequence sequence);
    
    /**
     * Translate sequence into {@link MemoryReference}.
     * @param sequence {@link Sequence}
     * @return {@link MemoryReference}
     */
    MemoryReference getBySequence(Sequence sequence);
    
    /**
     * Store a sequence memory reference mapping.
     * @param sequence {@link Sequence}
     * @param ref {@link MemoryReference}
     */
    void put(Sequence sequence, MemoryReference ref);
}
