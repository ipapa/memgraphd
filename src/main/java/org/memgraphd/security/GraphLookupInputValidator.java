package org.memgraphd.security;

import org.memgraphd.Graph;
import org.memgraphd.GraphRequestType;
import org.memgraphd.exception.GraphException;
/**
 * A helper interface to validate {@link Graph} lookup input data in isolation.
 * So far you can lookup data from the Graph using data id, decision sequence number
 * associated with that data, and the internal memory reference.
 * 
 * @author Ilirjan Papa
 * @since Janurary 31, 2013
 *
 * @param <D>
 */
public interface GraphLookupInputValidator<D> {
    
    /**
     * Throws a {@link GraphException} if validation fails.
     * @param inputData <D>
     * @throws GraphException
     */
    void validate(D inputData) throws GraphException;
    
    /**
     * Throws a {@link GraphException} if validation fails.
     * @param requestType {@link GraphRequestType}
     * @param inputData <D>
     * @throws GraphException
     */
    void validate(GraphRequestType requestType, D inputData) throws GraphException;

}
