package org.memgraphd.controller;

import org.memgraphd.data.GraphData;
import org.memgraphd.exception.GraphException;
import org.memgraphd.request.GraphDeleteRequest;
import org.memgraphd.request.GraphReadRequest;
import org.memgraphd.request.GraphRequest;
import org.memgraphd.request.GraphWriteRequest;
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
     * @param request {@link GraphWriteRequest}
     * @throws GraphException
     */
    void handle(GraphWriteRequest request) throws GraphException;
    
    /**
     * Handle a read-request to memgraphd.
     * @param request {@link GraphReadRequest}
     * @return {@link GraphData}
     * @throws GraphException
     */
    GraphData handle(GraphReadRequest request) throws GraphException;
    
    /**
     * Handle a delete-request to memgraphd.
     * @param request {@link GraphDeleteRequest}
     * @throws GraphException
     */
    void handle(GraphDeleteRequest request) throws GraphException;
    
}
