package org.memgraphd.memory;

import java.util.Set;

import org.memgraphd.data.GraphData;

/**
 * The memory buffer consists of a list of {@link MemoryLocation}s. Think of them as buckets
 * or place-holders for {@link GraphData}. They are initialized on startup and assigned a memory 
 * reference which will not change ever. They also serve a second role, which is establish
 * relationships between other memory locations serving as place-holders for related data.
 * 
 * @author Ilirjan Papa
 * @since July 31, 2012
 *
 */
public interface MemoryLocation {
    
    /**
     * Returns the memory reference of this memory location.
     * @return
     */
    MemoryReference reference();
    
    /**
     * Returns the {@link GraphData} instance stored in this memory location.
     * @return
     */
    GraphData data();
    
    /**
     * Returns the MemoryBlock that has claimed this memory location.
     * @return {@link MemoryBlock}
     */
    MemoryBlock block();
    
    /**
     * Returns a set of memory locations that are <i>direct links</i> of {@link GraphData} stored in
     * the memory location in question. 
     * <br><br>A good example: If the data stored in this memory location
     * is a Child object, that child object has links to Mother, Father objects.
     * 
     * @return {@link Set} of {@link MemoryLocation}
     */
    Set<MemoryLocation> links();
    
    /**
     * Returns a set of memory locations that are <i>indirect links</i> of {@link GraphData} stored in
     * the memory location in question. 
     * 
     * <br><br>A good example: If the data stored in this memory location
     * is a Father object, that parent object has references to Wife, Son, Daughter. Or another
     * way to see it: Wife, Son and Daughter have a direct link to Father. Inverse of {@link #links()}
     * 
     * @return {@link Set} of {@link MemoryLocation}
     * @return {@link Set} of {@link MemoryLocation}
     * 
     */
    Set<MemoryLocation> references();
    
}
