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
     * Checks to see if {@link Graph} is running.
     * @return true if it is running, false otherwise.
     */
    boolean isRunning();
    
    /**
     * Checks to see if {@link Graph} is stopped.
     * @return true if it is stopped, false otherwise.
     */
    boolean isStopped();
}
