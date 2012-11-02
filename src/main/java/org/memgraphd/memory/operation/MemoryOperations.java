package org.memgraphd.memory.operation;

import org.memgraphd.Graph;
import org.memgraphd.data.GraphData;
import org.memgraphd.memory.MemoryReference;

/**
 * Provides a list of all available memory operations.
 * 
 * @author Ilirjan Papa
 * @since November 1, 2012
 *
 */
public interface MemoryOperations {
    
    /**
     * Returns the {@link GraphData} stored in this memory reference.<br>
     * <b>NOTE:</b> This method will not produce a complete graph: GraphData + DataRelationships.
     * If you need the complete graph, you should call {@link #readGraph(MemoryReference)}.
     * 
     * @param reference {@link MemoryReference}
     * @return {@link GraphData}
     */
    GraphData read(MemoryReference reference);
    
    /**
     * Returns the {@link GraphData} and all its data relationships, creating a complete data graph.
     * @param reference {@link MemoryReference}
     * @return {@link GraphData}
     */
    GraphData readGraph(MemoryReference reference);
    
    /**
     * It will write the item in the {@link Graph} and it will return a reference to its location
     * for future references by the caller.
     * 
     * @param item {@link GraphData}
     * @return {@link MemoryReference}
     */
    MemoryReference write(GraphData item);
    
    /**
     * It will overwrite the content of the memory reference provided with the data passed.
     * @param ref {@link MemoryReference}
     * @param data {@link GraphData}
     */
    void update(MemoryReference ref, GraphData data);
    
    /**
     * Wipe out the data in this memory reference and recycle the reference.
     * @param ref {@link MemoryReference}
     */
    void free(MemoryReference ref);
    
    /**
     * The {@link GraphData} in the first reference will be linked to {@link GraphData} in link reference.
     * @param ref {@link MemoryReference}
     * @param link {@link MemoryReference}
     */
    void link(MemoryReference ref, MemoryReference link);
    
    /**
     * The opposite effect of {@link #link(MemoryReference, MemoryReference)}.
     * @param ref {@link MemoryReference}
     * @param link {@link MemoryReference}
     */
    void delink(MemoryReference ref, MemoryReference link);
    
    /**
     * Similar behavior as {@link #link(MemoryReference, MemoryReference)}
     * but in this case we are dealing with multiple links not just one.
     * @param ref {@link MemoryReference}
     * @param links {@link MemoryReference}
     */
    void link(MemoryReference ref, MemoryReference[] links);
    
    /**
     * The opposite effect of {@link #link(MemoryReference, MemoryReference[])}.
     * @param ref {@link MemoryReference}
     * @param links {@link MemoryReference}
     */
    void delink(MemoryReference ref, MemoryReference[] links);
    
    /**
     * Remove all links of {@link GraphData} with this memory reference.
     * @param ref {@link MemoryReference}
     */
    void delinkAll(MemoryReference ref);
    
    /**
     * Remove all references of {@link GraphData} with this memory reference.
     * @param ref
     */
    void dereferenceAll(MemoryReference ref);
}
