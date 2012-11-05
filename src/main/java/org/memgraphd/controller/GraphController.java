package org.memgraphd.controller;

import org.memgraphd.data.Data;
import org.memgraphd.data.GraphData;
import org.memgraphd.exception.GraphException;
/**
 * The {@link GraphController} acts as the middle man in charge of handling {@link GraphRequest}s for memgraphd data.
 * Clients will not have direct access to the Graph, they need to go through the {@link GraphController}.
 *  
 * @author Ilirjan Papa
 * @since August 17, 2012
 */
public interface GraphController {
    /**
     * Handles a write-request to memgraphd.
     * @param data {@link Data}
     * @throws GraphException
     */
    void write(Data data) throws GraphException;
    
    /**
     * Handle a read-request to memgraphd.
     * @param dataId {@link String}
     * @return {@link GraphData}
     * @throws GraphException
     */
    GraphData read(String dataId) throws GraphException;
    
    /**
     * Handle a delete-request to memgraphd.
     * @param dataId {@link String}
     * @throws GraphException
     */
    void delete(String dataId) throws GraphException;
    
}
