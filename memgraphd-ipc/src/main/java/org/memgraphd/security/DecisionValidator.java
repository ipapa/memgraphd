package org.memgraphd.security;

import org.apache.commons.lang.StringUtils;
import org.memgraphd.Graph;
import org.memgraphd.GraphRequestType;
import org.memgraphd.data.Data;
import org.memgraphd.decision.Decision;
import org.memgraphd.decision.DecisionMaker;
import org.memgraphd.exception.GraphException;

/**
 * Validates the {@link Decision}(s) made by the {@link Graph} to make sure they are valid.
 * @author Ilirjan Papa
 * @since January 31, 2013
 *
 */
public class DecisionValidator extends AbstractInputValidator {
    
    /**
     * Constructs a new instance of this validator.
     * @param decisionMaker {@link DecisionMaker}
     */
    public DecisionValidator(DecisionMaker decisionMaker) {
        super(decisionMaker);
    }
    
    /**
     * Validates a decision. It will throw a {@link GraphException} if decision state is invalid.
     * @param inputData {@link Decision}
     * @throws GraphException
     */
    public void validate(Decision inputData) throws GraphException {
        checkDecisionNotNull(inputData);
        checkDecisionSequence(inputData.getSequence());
        checkDecisionRequestType(inputData);
        checkDecisionData(inputData);
        checkDecisionTime(inputData);
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
        
        if(requestType.equals(GraphRequestType.READ)) {
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
