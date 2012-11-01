package org.memgraphd;
/**
 * {@link Graph}'s interface in charge of managing memgraphd's life-cycle event listeners.
 * 
 * @author Ilirjan Papa
 * @since September 4, 2012
 *
 */
public interface GraphLifecycleListenerManager {
    
    /**
     * You need to register in order to get a callback on startup/shutdown events.
     * @param handler {@link GraphLifecycleHandler}
     */
    void register(GraphLifecycleHandler handler);
    
    /**
     * Unregister if you no longer need to be notified on startup/shutdown events.
     * @param handler {@link GraphLifecycleHandler}
     */
    void unregister(GraphLifecycleHandler handler);
}
