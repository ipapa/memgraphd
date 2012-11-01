package org.memgraphd.controller;

import org.apache.log4j.Logger;
import org.memgraphd.Graph;
import org.memgraphd.GraphLifecycleHandler;
import org.memgraphd.data.Data;
import org.memgraphd.data.GraphData;
import org.memgraphd.data.GraphDataConverter;
import org.memgraphd.data.GraphDataImpl;
import org.memgraphd.decision.Decision;
import org.memgraphd.decision.DecisionMaker;
import org.memgraphd.exception.GraphException;
import org.memgraphd.request.GraphDeleteRequest;
import org.memgraphd.request.GraphReadRequest;
import org.memgraphd.request.GraphWriteRequest;

/**
 * The default implementation of a {@link GraphController}. Its responsibility is to two-fold:<br>
 * a. Handle all types of requests: read/write/delete.<br>
 * b. Act as a wrapper around Graph and manage its state on startup.<br>
 * 
 * @author Ilirjan Papa
 * @since August 17, 2012
 *
 */
public class GraphControllerImpl implements GraphController, GraphLifecycleHandler {
    private static final Logger LOGGER = Logger.getLogger(GraphControllerImpl.class);
    
    private final Graph graph;
    private final DecisionMaker decisionMaker;
    private final GraphDataConverter converter;
    private final GraphRequestValidator requestValidator;
    private final GraphDataInitializer dataInitializer;
    
    public GraphControllerImpl(Graph graph, DecisionMaker decisionMaker, 
            GraphRequestValidator requestValidator, GraphDataConverter converter) {
        this.graph = graph;
        this.decisionMaker = decisionMaker;
        this.converter = converter;
        this.requestValidator = requestValidator;
        this.dataInitializer = new GraphDataInitializer(graph, decisionMaker, converter);
    }

    @Override
    public void handle(GraphWriteRequest request) throws GraphException {
        // 1. You validate the request first
        requestValidator.validate(request);
        
        // 2. You forward the request to DecisionMaker for a decision.
        Decision decision = decisionMaker.decide(request);
        
        // 3. Read the data from request object
        Data data = converter.convert(request);
        
        // 4. Construct graph data object
        GraphData graphData = new GraphDataImpl(decision, data);
        
        // 5. Record this data in the brain
        graph.write(graphData);
        
    }

    @Override
    public GraphData handle(GraphReadRequest request) throws GraphException {
        // 1. First, you validate the request
        requestValidator.validate(request);
        
        // 2. Read the data from request object
        Data data = converter.convert(request);
        
        // 3. Read by id from Graph.
        return graph.readId(data.getId());
        
    }

    @Override
    public void handle(GraphDeleteRequest request) throws GraphException {
        // 1. First, you validate the request
        requestValidator.validate(request);
        
        // 2. Read the data from request object
        Data data = converter.convert(request);
        
        // 3. Read by id from Graph.
        GraphData graphData = graph.readId(data.getId());
        
        // 4. Handle error state when data is not there
        if(graphData == null) {
            throw new GraphException(String.format("No such data=%s exists.", data.getId()));
        }
        
        // 5. Issue the delete statement
        graph.delete(graphData);
    }

    @Override
    public synchronized void onStartup() {
        LOGGER.info("Starting the data initializer process on startup.");
        dataInitializer.initialize();
    }

    
    @Override
    public synchronized void onShutdown() {
        // TODO Auto-generated method stub
        
    }

}
