package org.memgraphd.controller;

import org.memgraphd.exception.GraphException;
import org.memgraphd.request.GraphRequest;
/**
 * {@link GraphController} will use this interface to validate incoming requests before they get handled.
 * @author Ilirjan Papa
 * @since August 1, 2012
 */
public interface GraphRequestValidator {
    
    /**
     * Validates the request to make sure it is a valid request. Throws an exception if request is invalid.
     * @param request {@link GraphRequest}
     * @throws GraphException
     */
    void validate(GraphRequest request) throws GraphException;
    
}
