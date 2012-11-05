package org.memgraphd.controller;

import org.apache.log4j.Logger;
import org.memgraphd.Graph;
import org.memgraphd.GraphImpl;
import org.memgraphd.GraphLifecycleHandler;
import org.memgraphd.GraphSupervisor;
import org.memgraphd.data.Data;
import org.memgraphd.data.GraphData;
import org.memgraphd.decision.Decision;
import org.memgraphd.decision.DecisionMaker;
import org.memgraphd.decision.SingleDecisionMaker;
import org.memgraphd.exception.GraphException;
import org.memgraphd.memory.MemoryManager;
import org.memgraphd.memory.MemoryManagerImpl;
import org.memgraphd.memory.MemoryStats;

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
    private final GraphSupervisor supervisor;
    private final MemoryStats memoryStats;
    private final DecisionMaker decisionMaker;

    private final GraphDataInitializer dataInitializer;
    
    public GraphControllerImpl(int capacity) {
        MemoryManager memoryManager = new MemoryManagerImpl(capacity);
        this.graph = new GraphImpl(memoryManager);
        this.supervisor = (GraphSupervisor) graph;
        this.memoryStats = (MemoryStats) memoryManager;
        this.decisionMaker = new SingleDecisionMaker();
        this.dataInitializer = new GraphDataInitializer(graph, decisionMaker);
        
        graph.register(this);
        
        graph.start();
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
        if(decisionMaker instanceof GraphLifecycleHandler) {
            GraphLifecycleHandler handler = (GraphLifecycleHandler) decisionMaker;
            handler.onStartup();
        }
        LOGGER.info("Starting the data initializer process on startup.");
        dataInitializer.initialize();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void onClearAll() {
        if(decisionMaker instanceof GraphLifecycleHandler) {
            GraphLifecycleHandler handler = (GraphLifecycleHandler) decisionMaker;
            handler.onClearAll();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void onShutdown() {
        if(decisionMaker instanceof GraphLifecycleHandler) {
            GraphLifecycleHandler handler = (GraphLifecycleHandler) decisionMaker;
            handler.onShutdown();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        supervisor.start();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        supervisor.stop();
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        supervisor.clear();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRunning() {
        return supervisor.isRunning();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isStopped() {
        return supervisor.isStopped();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return supervisor.isEmpty();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int capacity() {
        return memoryStats.capacity();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int occupied() {
        return memoryStats.occupied();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int available() {
        return memoryStats.available();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int recycled() {
        return memoryStats.recycled();
    }

}
