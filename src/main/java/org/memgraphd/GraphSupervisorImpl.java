package org.memgraphd;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Default implementation of {@link GraphSupervisor} which is responsible for starting/stopping
 * the service and alerting all listeners when such life-cycle events occur.
 * 
 * @author Ilirjan Papa
 * @since October 1, 2012
 *
 */
public abstract class GraphSupervisorImpl implements GraphSupervisor {
    private final List<GraphLifecycleHandler> listeners;
    private volatile GraphState state;
    
    public GraphSupervisorImpl() {
        this.listeners = new CopyOnWriteArrayList<GraphLifecycleHandler>();
        this.state = GraphState.INITIALIZED;
    }
    
    /**
     * {@inheritDoc}
     */
    public void register(GraphLifecycleHandler bean) {
        if(!listeners.contains(bean)) {
            listeners.add(bean);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void unregister(GraphLifecycleHandler bean) {
        listeners.remove(bean);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void start() {
        if(isRunning()) {
            throw new RuntimeException("memgraphd is already running");
        }
        
        state = GraphState.RUNNING;
        notifyOnStartup();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void stop() {
        if(isStopped()) {
            throw new RuntimeException("memgraphd is already stopped.");
        }
        if(!isRunning()) {
            throw new RuntimeException("memgraphd is not running.");
        }
        state = GraphState.STOPPED;
        notifyOnShutdown();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInitialized() {
        return GraphState.INITIALIZED.equals(state)
                || GraphState.RUNNING.equals(state);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRunning() {
        return GraphState.RUNNING.equals(state);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isStopped() {
        return GraphState.STOPPED.equals(state);
    }
    
    protected void notifyOnClearAll() {
        for(GraphLifecycleHandler h : listeners) {
            h.onClearAll();
        }
    }

    private void notifyOnStartup() {
        for(GraphLifecycleHandler h : listeners) {
            h.onStartup();
        }
    }

    private void notifyOnShutdown() {
        for(GraphLifecycleHandler h : listeners) {
            h.onShutdown();
        }
    }
}
