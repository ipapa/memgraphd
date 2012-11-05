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
}
