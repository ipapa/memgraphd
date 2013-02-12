package org.memgraphd.operation;

import org.memgraphd.GraphRequestType;
import org.memgraphd.data.Data;
import org.memgraphd.decision.Decision;
import org.memgraphd.decision.DecisionMaker;
import org.memgraphd.exception.GraphException;
import org.memgraphd.memory.MemoryReference;
import org.memgraphd.memory.operation.MemoryOperations;
import org.memgraphd.security.GraphAuthority;
import org.memgraphd.security.GraphRequestContext;
import org.memgraphd.security.GraphRequestResolver;
import org.memgraphd.security.GraphValidator;
/**
 * Base implementation for {@link GraphWriter}.
 * 
 * @author Ilirjan Papa
 * @since August 4, 2012
 *
 */
public class GraphWriterImpl extends AbstractGraphAccess implements GraphWriter {
    
    private final GraphRequestResolver requestResolver;
    private final GraphValidator validator;
    private final GraphAuthority authority;
    private final DecisionMaker decisionMaker;
    private final GraphStateManager stateManager;
    
    /**
     * 
     * @param memoryAccess {@link MemoryOperations}
     * @param authority {@link GraphAuthority}
     * @param validator {@link GraphValidator}
     * @param requestResolver {@link GraphRequestResolver}
     * @param decisionMaker {@link DecisionMaker}
     * @param stateManager {@link GraphStateManager}
     */
    public GraphWriterImpl(MemoryOperations memoryAccess, GraphAuthority authority, GraphValidator validator, 
                           GraphRequestResolver requestResolver, DecisionMaker decisionMaker, GraphStateManager stateManager) {
        super(memoryAccess);
        this.stateManager = stateManager;
        this.authority = authority;
        this.validator = validator;
        this.requestResolver = requestResolver;
        this.decisionMaker = decisionMaker;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MemoryReference write(Data data) throws GraphException {
        
        // 1. Create a request context
        GraphRequestContext context = requestResolver.resolve(GraphRequestType.CREATE, data);
        
        // 2. Validate the request context
        validator.validate(context);
        
        // 3. Authorize the request.
        authority.authorize(context);
        
        // 4. Generate a decision sequence for this request and log the transaction.
        Decision decision = decisionMaker.decidePutRequest(data);
        
        // 5. Update the state of the graph
        return stateManager.create(decision);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(String dataId) throws GraphException {
        // 1. Create the request context
        GraphRequestContext context = requestResolver.resolve(GraphRequestType.DELETE, dataId);
        
        // 2. Validate the request
        validator.validate(context);
        
        // 3. Authorize the request
        authority.authorize(context);
        
        // 4. Get a sequence assigned by decision maker to this delete request
        Decision decision = decisionMaker.decideDeleteRequest(context.getGraphData().getData());
        
        // 5. Delete the actual data
        stateManager.delete(decision, context.getGraphData());
      
    }

}
