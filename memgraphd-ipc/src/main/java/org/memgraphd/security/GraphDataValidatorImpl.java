package org.memgraphd.security;

import org.memgraphd.Graph;
import org.memgraphd.data.Data;
import org.memgraphd.data.DataValidator;
import org.memgraphd.decision.DecisionMaker;
import org.memgraphd.exception.GraphException;

/**
 * Validates the input {@link Data} that clients try to store in the {@link Graph}.
 * @author Ilirjan Papa
 * @since January 31, 2013
 *
 */
public class GraphDataValidatorImpl extends AbstractInputValidator implements GraphDataValidator{
    
    /**
     * Constructs a new instance of this validator.
     * @param decisionMaker {@link DecisionMaker}
     */
    public GraphDataValidatorImpl(DecisionMaker decisionMaker) {
        super(decisionMaker);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(GraphRequestContext requestContext) throws GraphException {
        checkRequestType(requestContext.getRequestType());
        switch (requestContext.getRequestType()) {
            case CREATE:
                checkDataNotNull(requestContext.getData());
                checkDataDoesNotAlreadyExist(requestContext);
                break;
                
            case UPDATE:
                checkDataNotNull(requestContext.getData());
                checkDataExists(requestContext);
                break;
                
            case DELETE:
                checkDataExists(requestContext);
                break;

            default:
                break;
        }
        runDataValidationRules(requestContext.getData());
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
    
    private void checkDataExists(GraphRequestContext context) throws GraphException {
        if(context.getGraphData() == null) {
            throw new GraphException("Data does not exist in the graph.");
        }
    }
    
    private void checkDataDoesNotAlreadyExist(GraphRequestContext context) throws GraphException {
        if(context.getGraphData() != null) {
            throw new GraphException("Data already exists in the graph.");
        }
    }
    
}
