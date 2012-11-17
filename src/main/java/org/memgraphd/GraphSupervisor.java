package org.memgraphd;

import org.memgraphd.data.GraphDataSnapshotManager;
import org.memgraphd.exception.GraphException;
import org.memgraphd.memory.MemoryStats;

/**
 * It is in charge of managing the state of the {@link Graph}.
 * 
 * @author Ilirjan Papa
 * @since October 1, 2012
 *
 */
public interface GraphSupervisor extends GraphLifecycleListenerManager, GraphDataSnapshotManager, MemoryStats {
    
    /**
     * Starts the {@link Graph} and changes the state to {@link GraphState#RUNNING}.
     * It also notifies all the listeners that {@link Graph} just started.
     * @throws GraphException
     */
    void run() throws GraphException;
    
    /**
     * Stops the {@link Graph} and changes the state to {@link GraphState#STOPPED}.
     * It also notifies all the listeners that {@link Graph} just stopped. 
     * @throws GraphException
     */
    void shutdown() throws GraphException;
    
    /**
     * Check to see if {@link Graph} is initialized before it can be started.
     * @return true if initialization completed, false otherwise.
     */
    boolean isInitialized();
    
    /**
     * Checks to see if {@link Graph} is running.
     * @return true if it is running, false otherwise.
     */
    boolean isRunning();
    
    /**
     * Checks to see if {@link Graph} is shut down.
     * @return true if it is shut down, false otherwise.
     */
    boolean isShutdown();
    
    /**
     * Returns true if the {@link Graph} is empty, has no data stored in it.
     * @return true if the {@link Graph} has not data stored in it, false otherwise.
     */
    boolean isEmpty();

}
