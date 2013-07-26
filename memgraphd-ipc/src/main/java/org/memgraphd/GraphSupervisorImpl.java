package org.memgraphd;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.memgraphd.data.GraphDataSnapshotManager;
import org.memgraphd.exception.GraphException;
import org.memgraphd.memory.MemoryStats;

/**
 * Default implementation of {@link GraphSupervisor} which is responsible for starting/stopping
 * the service and alerting all listeners when such life-cycle events occur.
 * 
 * @author Ilirjan Papa
 * @since October 1, 2012
 *
 */
public final class GraphSupervisorImpl implements GraphSupervisor {
    private final GraphDataSnapshotManager snapshotManager;
    private final MemoryStats memoryStats;
    private final List<GraphLifecycleHandler> listeners;
    private volatile GraphState state;
    
    public GraphSupervisorImpl(GraphDataSnapshotManager snapshotManager, MemoryStats memoryStats) {
        this.snapshotManager = snapshotManager;
        this.listeners = new CopyOnWriteArrayList<GraphLifecycleHandler>();
        this.memoryStats = memoryStats;
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
    public synchronized void run() throws GraphException {
        notifyOnStartup();
        state = GraphState.RUNNING;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void shutdown() throws GraphException {
        notifyOnShutdown();
        state = GraphState.STOPPED;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize() throws GraphException {
        snapshotManager.initialize();
        state = GraphState.INITIALIZED;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() throws GraphException {
        snapshotManager.clear();
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
    public boolean isShutdown() {
        return GraphState.STOPPED.equals(state);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isEmpty() {
        return memoryStats.occupied() == 0;
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
