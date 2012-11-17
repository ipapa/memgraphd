package org.memgraphd;
/**
 * Implement this interface and register with {@link Graph} in order to get a callback when memgraphd
 * starts up or shuts down.
 * 
 * @author Ilirjan Papa
 * @since September 4, 2012
 * 
 * @see Graph
 * @see GraphLifecycleListenerManager
 * @see GraphSupervisor
 */
public interface GraphLifecycleHandler {
    /**
     * Actions to take right after memgraphd was started up.
     */
    void onStartup();
    
    /**
     * Actions to take right after memgraphd was shut down.
     */
    void onShutdown();
    
}
