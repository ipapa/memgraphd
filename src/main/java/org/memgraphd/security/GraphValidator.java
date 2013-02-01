package org.memgraphd.security;

import org.memgraphd.Graph;
import org.memgraphd.GraphRequestType;
import org.memgraphd.data.Data;
import org.memgraphd.data.DataValidator;
import org.memgraphd.decision.Decision;
import org.memgraphd.decision.Sequence;
import org.memgraphd.exception.GraphException;
import org.memgraphd.memory.MemoryReference;

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
     * It will throw a {@link GraphException} if the state of the data is not valid.
     * @param data {@link Data}
     * @throws GraphException
     */
    void validate(Data data) throws GraphException;
    
    /**
     * Validates the decision instance to make sure it is valid.
     * @param decision {@link Decision}
     * @throws GraphException
     */
    void validate(Decision decision) throws GraphException;
    
    /**
     * Validates that the dataId provided is valid. Validation rules vary by request type.
     * @param requestType {@link GraphRequestType}
     * @param dataId {@link String}
     * @throws GraphException
     */
    void validate(GraphRequestType requestType, String dataId) throws GraphException;
    
    /**
     * Validate that the sequence in question is valid. Validation rules vary  by request type.
     * @param requestType {@link GraphRequestType}
     * @param sequence {@link Sequence}
     * @throws GraphException
     */
    void validate(GraphRequestType requestType, Sequence sequence) throws GraphException;
    
    /**
     * Validate that the memory reference provided is valid. Validation rules vary by request type.
     * @param requestType {@link GraphRequestType}
     * @param reference {@link MemoryReference}
     * @throws GraphException
     */
    void validate(GraphRequestType requestType, MemoryReference reference) throws GraphException;
    
}
