package org.memgraphd.operation;

import org.apache.commons.lang.StringUtils;
import org.memgraphd.Graph;
import org.memgraphd.GraphRequestType;
import org.memgraphd.data.Data;
import org.memgraphd.data.DataValidator;
import org.memgraphd.decision.Decision;
import org.memgraphd.decision.DecisionMaker;
import org.memgraphd.decision.Sequence;
import org.memgraphd.exception.GraphException;
import org.memgraphd.memory.MemoryReference;

/**
 * The default implementation that the {@link Graph} will use to validate {@link Data} stored in it.
 * The validation rules will only be enforced on {@link GraphRequestType#CREATE}, 
 * {@link GraphRequestType#UPDATE} and {@link GraphRequestType#DELETE}.<br><br>
 * 
 * <b>NOTE:</b> Validation rules that we enforce might vary by the type of request and input data.
 * @author Ilirjan Papa
 * @since January 30, 2013
 *
 */
public class GraphValidatorImpl implements GraphValidator {
    
    private final DecisionMaker decisionMaker;
    private final GraphReader reader;
    
    /**
     * Default constructor.
     */
    public GraphValidatorImpl(DecisionMaker decisionMaker, GraphReader reader) {
        this.decisionMaker = decisionMaker;
        this.reader = reader;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void validate(Data data) throws GraphException {
        // This is the only rule we enforce - you cannot store null objects in the graph and the
        // id of that data cannot be null or empty.
        checkDataNotNull(data);
        
        // These are validation rules defined by the data itself - if any.
        runDataValidationRules(data);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void validate(Decision decision) throws GraphException {
        checkDecisionNotNull(decision);
        checkDecisionSequence(decision.getSequence());
        checkDecisionRequestType(decision);
        checkDecisionData(decision);
        checkDecisionTime(decision);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void validate(GraphRequestType requestType, String dataId) throws GraphException {
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
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void validate(GraphRequestType requestType, Sequence sequence) throws GraphException {
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
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void validate(GraphRequestType requestType, MemoryReference reference)
            throws GraphException {
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
    
    private void runDataValidationRules(Data data) throws GraphException {
        if(data instanceof DataValidator) {
            if(!((DataValidator) data).isValid()) {
                throw new GraphException("Data validation failed.");
            }
        }
    }

    private void checkDataNotNull(Data data) throws GraphException {
        if(data == null) {
            throw new GraphException("Data is null.");
        }
        
        if(StringUtils.isBlank(data.getId())) {
            throw new GraphException("Data id is invalid.");
        }
    }
    
    private void checkDecisionNotNull(Decision decision) throws GraphException {
        if(decision == null) {
            throw new GraphException("Decision is null.");
        }
    }
    
    private void checkDecisionSequence(Sequence sequence) throws GraphException {
        
        if(sequence == null) {
            throw new GraphException("Decision is missing a sequence number.");
        }
        
        Sequence latestSequence = decisionMaker.latestDecision();
        if(sequence.number() > latestSequence.number()) {
            throw new GraphException("Decision sequence number is out of range.");
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
    
    private void checkRequestType(GraphRequestType type) throws GraphException {
        
        if(type == null) {
            throw new GraphException("Failed to resolve this request.");
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
    
    private boolean dataExists(String dataId) {
        return reader.readId(dataId) != null;
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
    
    private boolean dataExists(Sequence sequence) {
        return reader.readSequence(sequence) != null;
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
    
    private boolean dataExists(MemoryReference reference) {
        return reader.readReference(reference) != null;
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