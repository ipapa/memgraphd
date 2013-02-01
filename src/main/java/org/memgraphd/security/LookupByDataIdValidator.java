package org.memgraphd.security;

import org.apache.commons.lang.StringUtils;
import org.memgraphd.GraphRequestType;
import org.memgraphd.decision.DecisionMaker;
import org.memgraphd.exception.GraphException;
import org.memgraphd.operation.GraphReader;
/**
 * Validates the input data provided by clients to lookup data by data id.
 * @author Ilirjan Papa
 * @since January 31, 2013
 *
 */
public class LookupByDataIdValidator extends AbstractLookupInputValidator implements GraphLookupInputValidator<String> {
    
    public LookupByDataIdValidator(DecisionMaker decisionMaker, GraphReader reader) {
        super(decisionMaker, reader);
    }

    @Override
    public void validate(String inputData) throws GraphException {
        checkDataIdNotNull(inputData);
    }

    @Override
    public void validate(GraphRequestType requestType, String dataId) throws GraphException {
        checkRequestType(requestType);
        checkDataId(dataId);
        
        switch (requestType) {
            case CREATE:
                checkDataDoesNotExists(dataId);
                break;
                
            case UPDATE:
                checkDataExists(dataId);
                break;
                
            case DELETE:
                checkDataExists(dataId);
                break;

            default:
                break;
        }
        
    }
    
    private void checkDataId(String dataId) throws GraphException {
        
        if(StringUtils.isBlank(dataId)) {
            throw new GraphException(String.format("Data id is null or empty.", dataId));
        }
    }
    
    private void checkDataExists(String dataId) throws GraphException {
        if(!dataExists(dataId)) {
            throw new GraphException(String.format("Data id=%s does not exist in the graph.", dataId));
        }
    }
    
    private void checkDataDoesNotExists(String dataId) throws GraphException {
        if(dataExists(dataId)) {
            throw new GraphException(String.format("Data id=%s already exists in the graph.", dataId));
        }
    }

}
