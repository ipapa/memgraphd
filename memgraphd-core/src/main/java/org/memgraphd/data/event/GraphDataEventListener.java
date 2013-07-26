package org.memgraphd.data.event;

import java.util.EventListener;

import org.memgraphd.Graph;
/**
 * In order to register as a data-event listener with the {@link Graph} you need to implement this
 * interface.
 * 
 * @author Ilirjan Papa
 * @since September 12, 2012
 *
 */
public interface GraphDataEventListener extends EventListener {
    
    /**
     * Returns an instance of {@link GraphDataEventHandler} in charge of handling the event.
     * @return {@link GraphDataEventHandler}
     */
    GraphDataEventHandler getHandler();
    
}
