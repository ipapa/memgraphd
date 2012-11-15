package org.memgraphd;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;
import org.memgraphd.memory.MemoryStats;

/**
 * Default implementation of {@link GraphSupervisor} which is responsible for starting/stopping
 * the service and alerting all listeners when such life-cycle events occur.
 * 
 * @author Ilirjan Papa
 * @since October 1, 2012
 *
 */
public abstract class GraphSupervisorImpl implements GraphSupervisor {

    protected static final Logger LOGGER = Logger.getLogger(GraphImpl.class);
    
    private final List<GraphLifecycleHandler> listeners;
    private volatile GraphState state;
    
    public GraphSupervisorImpl() {
        this.listeners = new CopyOnWriteArrayList<GraphLifecycleHandler>();
        this.state = GraphState.INITIALIZED;
    }
    
    protected abstract MemoryStats getMemoryStats();
    
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
        state = GraphState.RUNNING;
        notifyOnStartup();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void stop() {
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

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isEmpty() {
        return getMemoryStats().occupied() == 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int capacity() {
        return getMemoryStats().capacity();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int occupied() {
        return getMemoryStats().occupied();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int available() {
        return getMemoryStats().available();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int recycled() {
        return getMemoryStats().recycled();
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
