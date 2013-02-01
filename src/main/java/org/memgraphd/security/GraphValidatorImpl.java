package org.memgraphd.security;

import org.memgraphd.Graph;
import org.memgraphd.GraphRequestType;
import org.memgraphd.data.Data;
import org.memgraphd.decision.Decision;
import org.memgraphd.decision.DecisionMaker;
import org.memgraphd.decision.Sequence;
import org.memgraphd.exception.GraphException;
import org.memgraphd.memory.MemoryReference;
import org.memgraphd.operation.GraphReader;

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
    
    private final GraphLookupInputValidator<String> dataIdValidator;
    private final GraphLookupInputValidator<Sequence> sequenceValidator;
    private final GraphLookupInputValidator<MemoryReference> memoryReferenceValidator;
    private final GraphLookupInputValidator<Decision> decisionValidator;
    private final GraphLookupInputValidator<Data> dataValidator;
    
    /**
     * Default constructor.
     */
    public GraphValidatorImpl(DecisionMaker decisionMaker, GraphReader reader) {
        this.dataIdValidator = new LookupByDataIdValidator(decisionMaker, reader);
        this.sequenceValidator = new LookupBySequenceValidator(decisionMaker, reader);
        this.memoryReferenceValidator = new LookupByMemoryReferenceValidator(decisionMaker, reader);
        this.decisionValidator = new DecisionValidator(decisionMaker, reader);
        this.dataValidator = new InputDataValidator(decisionMaker, reader);
    }
       /**
     * {@inheritDoc}
     */
    @Override
    public final void validate(Data data) throws GraphException {
        dataValidator.validate(data);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void validate(Decision decision) throws GraphException {
        decisionValidator.validate(decision);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void validate(GraphRequestType requestType, String dataId) throws GraphException {
        dataIdValidator.validate(requestType, dataId);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void validate(GraphRequestType requestType, Sequence sequence) throws GraphException {
       sequenceValidator.validate(requestType, sequence);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void validate(GraphRequestType requestType, MemoryReference reference)
            throws GraphException {
        memoryReferenceValidator.validate(requestType, reference);
    }    
}