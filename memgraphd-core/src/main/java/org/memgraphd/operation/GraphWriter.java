package org.memgraphd.operation;

import org.memgraphd.Graph;
import org.memgraphd.data.Data;
import org.memgraphd.exception.GraphException;
import org.memgraphd.memory.MemoryReference;
/**
 * Responsible for handling all write and delete events in the {@link Graph}.
 * 
 * @author Ilirjan Papa
 * @since August 4, 2012
 *
 */
public interface GraphWriter {
    
    /**
     * Write {@link Data} into the {@link Graph} and return the {@link MemoryReference} assigned
     * to this data instance for future references by the caller. If data already exists in the
     * Graph, the operation will fail and an exception will be thrown.
     * @param data {@link Data}
     * @return {@link MemoryReference}
     * @throws GraphException
     */
    MemoryReference create(Data data) throws GraphException;
    
    /**
     * Update existing {@link Data} in the {@link Graph} and return the {@link MemoryReference} assigned
     * to this data instance for future references by the caller. If data does not exist in the Graph, the operation
     * will fail and an exception will be thrown.
     * @param data {@link Data}
     * @return {@link MemoryReference}
     * @throws GraphException
     */
    MemoryReference update(Data data) throws GraphException;
    
    /**
     * Delete {@link Data} from the Graph. If data does not exist in the Graph, the operation will
     * fail and an exception will be thrown.
     * @param data {@link Data}
     * @throws GraphException
     */
    void delete(Data data) throws GraphException;
    
    /**
     * Delete {@link Data} from the Graph using the data id to lookup the data in the Graph. If the 
     * data does not exist in the Graph, the operation will fail and an exception will be thrown.
     * @param dataId {@link String}
     * @throws GraphException
     */
    void delete(String dataId) throws GraphException;
    
    
}
