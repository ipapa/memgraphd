package org.memgraphd.controller;

import org.apache.log4j.Logger;
import org.memgraphd.Graph;
import org.memgraphd.GraphLifecycleHandler;
import org.memgraphd.data.Data;
import org.memgraphd.data.GraphData;
import org.memgraphd.decision.Decision;
import org.memgraphd.decision.DecisionMaker;
import org.memgraphd.exception.GraphException;

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

    private final GraphDataInitializer dataInitializer;
    
    public GraphControllerImpl(Graph graph, DecisionMaker decisionMaker) {
        this.graph = graph;
        this.decisionMaker = decisionMaker;
        this.dataInitializer = new GraphDataInitializer(graph, decisionMaker);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void write(Data data) throws GraphException {
        
        // 1. You forward the request to DecisionMaker for a decision.
        Decision decision = decisionMaker.decideWriteRequest(data);
        
        // 2. Record this data in the brain
        graph.write(decision);
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GraphData read(String dataId) throws GraphException {
        // 1. Read by id from Graph.
        return graph.readId(dataId);
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(String dataId) throws GraphException {

        // 1. Read by id from Graph.
        GraphData graphData = graph.readId(dataId);
        
        // 2. Handle error state when data is not there
        if(graphData == null) {
            throw new GraphException(String.format("No such data=%s exists.", dataId));
        }
        
        // 3. Issue the delete statement
        graph.delete(graphData);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void onStartup() {
        LOGGER.info("Starting the data initializer process on startup.");
        dataInitializer.initialize();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void onClearAll() {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void onShutdown() {
    }

}
