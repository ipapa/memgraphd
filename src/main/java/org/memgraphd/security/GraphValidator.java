package org.memgraphd.security;

import org.memgraphd.Graph;
import org.memgraphd.data.Data;
import org.memgraphd.data.DataValidator;
import org.memgraphd.decision.Decision;
import org.memgraphd.exception.GraphException;

/**
 * It is responsible for validating all the {@link Data} that is written in the {@link Graph}. That 
 * means respecting the validation rules that the {@link Data} itself defines with {@link DataValidator}
 * interface.
 * 
 * @author Ilirjan Papa
 * @since January 30, 2013
 *
 */
public interface GraphValidator {
    
    /**
     * Validates the decision instance to make sure it is valid.
     * @param decision {@link Decision}
     * @throws GraphException
     */
    void validate(Decision decision) throws GraphException;
    
    /**
     * It will throw a {@link GraphException} if the graph request context is invalid.
     * @param context {@link GraphRequestContext}
     * @throws GraphException
     */
    void validate(GraphRequestContext context) throws GraphException;
    
}
