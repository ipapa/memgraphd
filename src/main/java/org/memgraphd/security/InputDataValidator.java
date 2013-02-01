package org.memgraphd.security;

import org.memgraphd.Graph;
import org.memgraphd.GraphRequestType;
import org.memgraphd.data.Data;
import org.memgraphd.data.DataValidator;
import org.memgraphd.decision.DecisionMaker;
import org.memgraphd.exception.GraphException;
import org.memgraphd.operation.GraphReader;

/**
 * Validates the input {@link Data} that clients try to store in the {@link Graph}.
 * @author Ilirjan Papa
 * @since January 31, 2013
 *
 */
public class InputDataValidator extends AbstractLookupInputValidator implements GraphLookupInputValidator<Data>{
    
    /**
     * Constructs a new instance of this validator.
     * @param decisionMaker {@link DecisionMaker}
     * @param reader {@link GraphReader}
     */
    public InputDataValidator(DecisionMaker decisionMaker, GraphReader reader) {
        super(decisionMaker, reader);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void validate(Data inputData) throws GraphException {
        checkDataNotNull(inputData);
        runDataValidationRules(inputData);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void validate(GraphRequestType requestType, Data inputData) throws GraphException {
        checkRequestType(requestType);
        validate(inputData);
    }
    
    private void checkDataNotNull(Data data) throws GraphException {
        if(data == null) {
            throw new GraphException("Data is null.");
        }
        
        checkDataIdNotNull(data.getId());
    }
    
    private void runDataValidationRules(Data data) throws GraphException {
        if(data instanceof DataValidator) {
            if(!((DataValidator) data).isValid()) {
                throw new GraphException("Data validation failed.");
            }
        }
    }
    
}
