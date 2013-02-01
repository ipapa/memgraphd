package org.memgraphd.security;

import org.apache.commons.lang.StringUtils;
import org.memgraphd.Graph;
import org.memgraphd.GraphRequestType;
import org.memgraphd.data.Data;
import org.memgraphd.decision.Decision;
import org.memgraphd.decision.DecisionMaker;
import org.memgraphd.exception.GraphException;
import org.memgraphd.operation.GraphReader;

/**
 * Validates the {@link Decision} made by the {@link Graph} to make sure they are valid.
 * @author Ilirjan Papa
 * @since January 31, 2013
 *
 */
public class DecisionValidator extends AbstractLookupInputValidator implements GraphLookupInputValidator<Decision>{
    
    /**
     * Constructs a new instance of this validator.
     * @param decisionMaker {@link DecisionMaker}
     * @param reader {@link GraphReader}
     */
    public DecisionValidator(DecisionMaker decisionMaker, GraphReader reader) {
        super(decisionMaker, reader);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void validate(Decision inputData) throws GraphException {
        checkDecisionNotNull(inputData);
        checkDecisionSequence(inputData.getSequence());
        checkDecisionRequestType(inputData);
        checkDecisionData(inputData);
        checkDecisionTime(inputData);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void validate(GraphRequestType requestType, Decision inputData) throws GraphException {
        checkRequestType(requestType);
        validate(inputData);
        
    }
    
    private void checkDecisionNotNull(Decision decision) throws GraphException {
        if(decision == null) {
            throw new GraphException("Decision is null.");
        }
    }
    
    private void checkDecisionRequestType(Decision decision) throws GraphException {
        GraphRequestType requestType = decision.getRequestType();
        
        if(requestType == null) {
            throw new GraphException("Decision is missing request type object.");
        }
        
        if(requestType.equals(GraphRequestType.RETRIEVE)) {
            throw new GraphException("Decision has an unsupported request type.");
        }
    }
    
    private void checkDecisionData(Decision decision) throws GraphException {
        Data data = decision.getData();
        
        if(data == null) {
            throw new GraphException("Decision data is null.");
        }
        
        if(StringUtils.isBlank(data.getId()) || StringUtils.isBlank(decision.getDataId())) {
            throw new GraphException("Decision data id is null or empty.");
        }
        
        if(!data.getId().equals(decision.getDataId())) {
            throw new GraphException("Decision dataId does not match with data's id.");
        }
    }
    
    private void checkDecisionTime(Decision decision) throws GraphException {
        
        if(decision.getTime() == null) {
            throw new GraphException("Decision is missing the time decision was made.");
        }
    }

}
