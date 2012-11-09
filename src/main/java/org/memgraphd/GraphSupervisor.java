package org.memgraphd;
/**
 * It is in charge of managing the state of the {@link Graph}.
 * 
 * @author Ilirjan Papa
 * @since Oct 1, 2012
 *
 */
public interface GraphSupervisor {
    
    /**
     * Starts the {@link Graph} and changes the state to {@link GraphState#RUNNING}.
     * It also notifies all the listeners that {@link Graph} just started.
     */
    void start();
    
    /**
     * Stops the {@link Graph} and changes the state to {@link GraphState#STOPPED}.
     * It also notifies all the listeners that {@link Graph} just stopped. 
     */
    void stop();
    
    /**
     * Wipes out all the data from the {@link Graph}.<br>
     * <b>WARNING: This will permanently wipe out all the data you have stored in this {@link Graph}.</b> 
     */
    void clear();
    
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
     * Checks to see if {@link Graph} is stopped.
     * @return true if it is stopped, false otherwise.
     */
    boolean isStopped();
    
    /**
     * Returns true if the {@link Graph} is empty, has no data stored in it.
     * @return true if the {@link Graph} has not data stored in it, false otherwise.
     */
    boolean isEmpty();
    
    /**
     * Register here to be notified when the {@link Graph} start/stops.
     * @param bean {@link GraphLifecycleHandler}
     */
    public void register(GraphLifecycleHandler bean);
    
    /**
     * Unregister this bean if the bean no longer wants to be notified of start/stop events.
     * @param bean {@link GraphLifecycleHandler}
     */
    public void unregister(GraphLifecycleHandler bean);
}
