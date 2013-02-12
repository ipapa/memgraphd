package org.memgraphd.security;

import org.memgraphd.exception.GraphException;
/**
 * It validates a {@link GraphRequestContext} to make sure that the input data
 * provided by the caller is valid and the internal state of the existing data 
 * allows such request to take place.
 * 
 * @author Ilirjan Papa
 * @since Janurary 31, 2013
 *
 */
public interface GraphDataValidator {
    
    /**
     * Throws a {@link GraphException} if validation fails.
     * @param requestContext {@link GraphRequestContext}
     * @throws GraphException
     */
    void validate(GraphRequestContext requestContext) throws GraphException;

}
