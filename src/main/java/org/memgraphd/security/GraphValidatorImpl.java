package org.memgraphd.security;

import org.memgraphd.Graph;
import org.memgraphd.GraphRequestType;
import org.memgraphd.data.Data;
import org.memgraphd.decision.Decision;
import org.memgraphd.decision.DecisionMaker;
import org.memgraphd.exception.GraphException;

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
    
    private final DecisionValidator decisionValidator;
    private final GraphDataValidator dataValidator;
    
    /**
     * Default constructor.
     */
    public GraphValidatorImpl(DecisionMaker decisionMaker) {
        this.decisionValidator = new DecisionValidator(decisionMaker);
        this.dataValidator = new GraphDataValidatorImpl(decisionMaker);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(Decision decision) throws GraphException {
        decisionValidator.validate(decision);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void validate(GraphRequestContext context) throws GraphException {
        dataValidator.validate(context);
    }
    
   
}