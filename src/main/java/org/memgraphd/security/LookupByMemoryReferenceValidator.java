package org.memgraphd.security;

import org.memgraphd.GraphRequestType;
import org.memgraphd.decision.DecisionMaker;
import org.memgraphd.exception.GraphException;
import org.memgraphd.memory.MemoryReference;
import org.memgraphd.operation.GraphReader;
/**
 * Validates the input data provided by clients to lookup data by data id.
 * @author Ilirjan Papa
 * @since January 31, 2013
 *
 */
public class LookupByMemoryReferenceValidator extends AbstractLookupInputValidator implements GraphLookupInputValidator<MemoryReference> {
    
    /**
     * Constructs a new instance of this validator.
     * @param decisionMaker {@link DecisionMaker}
     * @param reader {@link GraphReader}
     */
    public LookupByMemoryReferenceValidator(DecisionMaker decisionMaker, GraphReader reader) {
        super(decisionMaker, reader);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(MemoryReference reference) throws GraphException {
        checkMemoryReference(reference);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(GraphRequestType requestType, MemoryReference reference) throws GraphException {
        checkRequestType(requestType);
        checkMemoryReference(reference);
        
        switch (requestType) {
            case CREATE:
                checkDataDoesNotExists(reference);
                break;
                
            case UPDATE:
                checkDataExists(reference);
                break;
                
            case DELETE:
                checkDataExists(reference);
                break;

            default:
                break;
        }
    }
    
    private void checkDataExists(MemoryReference reference) throws GraphException {
        if(!dataExists(reference)) {
            throw new GraphException(String.format("Memory reference id=%d does not exist in the graph.", reference.id()));
        }
    }
    
    private void checkDataDoesNotExists(MemoryReference reference) throws GraphException {
        if(dataExists(reference)) {
            throw new GraphException(String.format("Memory reference id=%d already exists in the graph.", reference.id()));
        }
    }
    
    private void checkMemoryReference(MemoryReference reference) throws GraphException {
        if(reference == null) {
            throw new GraphException("Memory reference id is null.");
        }
        
        if(reference.id() < 0) {
            throw new GraphException("Memory reference id is invalid.");
        }
    }
}
