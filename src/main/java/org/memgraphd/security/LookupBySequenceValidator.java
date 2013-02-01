package org.memgraphd.security;

import org.memgraphd.GraphRequestType;
import org.memgraphd.decision.DecisionMaker;
import org.memgraphd.decision.Sequence;
import org.memgraphd.exception.GraphException;
import org.memgraphd.operation.GraphReader;
/**
 * Validates the input data provided by clients to lookup data by {@link Sequence}.
 * @author Ilirjan Papa
 * @since January 31, 2013
 *
 */
public class LookupBySequenceValidator extends AbstractLookupInputValidator implements GraphLookupInputValidator<Sequence> {
    
    /**
     * Constructs a new instance of this validator.
     * @param decisionMaker {@link DecisionMaker}
     * @param reader {@link GraphReader}
     */
    public LookupBySequenceValidator(DecisionMaker decisionMaker, GraphReader reader) {
        super(decisionMaker, reader);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(Sequence inputData) throws GraphException {
        checkDecisionSequence(inputData);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(GraphRequestType requestType, Sequence sequence) throws GraphException {
        checkRequestType(requestType);
        checkDecisionSequence(sequence);
        
        switch (requestType) {
            case CREATE:
                checkDataDoesNotExists(sequence);
                break;
                
            case UPDATE:
                checkDataExists(sequence);
                break;
                
            case DELETE:
                checkDataExists(sequence);
                break;

            default:
                break;
        }
    }
    
    private void checkDataExists(Sequence sequence) throws GraphException {
        if(!dataExists(sequence)) {
            throw new GraphException(String.format("Data with sequence id=%d does not exist in the graph.", sequence.number()));
        }
    }
    
    private void checkDataDoesNotExists(Sequence sequence) throws GraphException {
        if(dataExists(sequence)) {
            throw new GraphException(String.format("Data with sequence id=%d already exists in the graph.", sequence.number()));
        }
    }
    
}
