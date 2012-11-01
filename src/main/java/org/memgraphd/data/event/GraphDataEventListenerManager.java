package org.memgraphd.data.event;

import org.memgraphd.Graph;

/**
 * Manages {@link GraphDataEventListener}s on behalf of the {@link Graph}. It also
 * acts as a listener of its own, so that it can handle all call-backs to listeners.
 * 
 * @author Ilirjan Papa
 * @since September 13, 2012
 *
 */
public interface GraphDataEventListenerManager extends GraphDataEventHandler {
    
    /**
     * To listen in for graph data events you need to register here. 
     * @param event {@link GraphDataEventListener}
     */
    void addEventListener(GraphDataEventListener event);
    
    /**
     * If you no longer need to be notified of graph data events, you need to call this method.
     * @param event {@link GraphDataEventListener}
     */
    void removeEventListener(GraphDataEventListener event);
}
