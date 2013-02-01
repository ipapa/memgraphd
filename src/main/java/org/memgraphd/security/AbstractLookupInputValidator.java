package org.memgraphd.security;

import org.apache.commons.lang.StringUtils;
import org.memgraphd.GraphRequestType;
import org.memgraphd.decision.DecisionMaker;
import org.memgraphd.decision.Sequence;
import org.memgraphd.exception.GraphException;
import org.memgraphd.memory.MemoryReference;
import org.memgraphd.operation.GraphReader;
/**
 * Abstract class that all implementations of {@link GraphLookupInputValidator} should extend.
 * @author Ilirjan Papa
 * @since January 31, 2013
 *
 */
public abstract class AbstractLookupInputValidator {
    private final DecisionMaker decisionMaker;
    private final GraphReader reader;
    
    /**
     * Default constructor.
     */
    public AbstractLookupInputValidator(DecisionMaker decisionMaker, GraphReader reader) {
        this.decisionMaker = decisionMaker;
        this.reader = reader;
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
    
    protected boolean dataExists(String dataId) {
        return reader.readId(dataId) != null;
    }
    
    protected boolean dataExists(MemoryReference reference) {
        return reader.readReference(reference) != null;
    }
    
    protected boolean dataExists(Sequence sequence) {
        return reader.readSequence(sequence) != null;
    }
}
