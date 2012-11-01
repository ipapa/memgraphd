package org.memgraphd.controller;

import org.apache.commons.lang.StringUtils;
import org.memgraphd.exception.GraphException;
import org.memgraphd.request.GraphRequest;

/**
 * Simple {@link GraphRequestValidator} implementation that checks to make sure that {@link GraphRequest#getRequestUri()} is set.
 * If it is not set, it will throw a {@link GraphException}.
 * 
 * @author Ilirjan Papa
 * @since August 30, 2012
 *
 */
public class GraphRequestValidatorImpl implements GraphRequestValidator {
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(GraphRequest request) throws GraphException {
        if(request == null) {
            throw new GraphException("Validation failed: Request is null");
        }
        
        if(StringUtils.isBlank(request.getRequestUri())) {
            throw new GraphException("Validation failed: Request has null or empty requestUri");
        }
    }

}
