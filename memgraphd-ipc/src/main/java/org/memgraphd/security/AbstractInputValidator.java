package org.memgraphd.security;

import org.apache.commons.lang.StringUtils;
import org.memgraphd.GraphRequestType;
import org.memgraphd.decision.DecisionMaker;
import org.memgraphd.decision.Sequence;
import org.memgraphd.exception.GraphException;
/**
 * Abstract class that all implementations of {@link GraphDataValidator} should extend.
 * @author Ilirjan Papa
 * @since January 31, 2013
 *
 */
public abstract class AbstractInputValidator {
    private final DecisionMaker decisionMaker;
    
    /**
     * Constructs a new instance.
     * @param decisionMaker {@link DecisionMaker}
     */
    public AbstractInputValidator(DecisionMaker decisionMaker) {
        this.decisionMaker = decisionMaker;
    }
    
    protected void checkRequestType(GraphRequestType type) throws GraphException {
        
        if(type == null) {
            throw new GraphException("Failed to resolve this request.");
        }
    }
    
    protected void checkDataIdNotNull(String dataId) throws GraphException {
        if(StringUtils.isBlank(dataId)) {
            throw new GraphException("Data id is invalid.");
        }
    }
    
    protected void checkDecisionSequence(Sequence sequence) throws GraphException {
        
        if(sequence == null) {
            throw new GraphException("Decision is missing a sequence number.");
        }
        
        checkSequenceInRange(sequence);
    }

    private void checkSequenceInRange(Sequence sequence) throws GraphException {
        Sequence latestSequence = decisionMaker.latestDecision();
        if(sequence.number() > latestSequence.number()) {
            throw new GraphException("Decision sequence number is out of range.");
        }
    }

}
