package org.memgraphd.data.event;

/**
 * Default implementation of {@link GraphDataEventListener}.
 * 
 * @author Ilirjan Papa
 * @since September 12, 2012
 *
 */
public class GraphDataEventListenerImpl implements GraphDataEventListener {
    
    private final GraphDataEventHandler handler;
    
    public GraphDataEventListenerImpl(GraphDataEventHandler handler) {
        this.handler = handler;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GraphDataEventHandler getHandler() {
        return handler;
    }

}
