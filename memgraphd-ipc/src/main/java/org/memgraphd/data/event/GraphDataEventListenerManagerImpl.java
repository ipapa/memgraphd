package org.memgraphd.data.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.memgraphd.data.GraphData;

/**
 * Default implementation of {@link GraphDataEventListenerManager}.
 * 
 * @author Ilirjan Papa
 * @since September 14, 2012
 *
 */
public class GraphDataEventListenerManagerImpl implements GraphDataEventListenerManager {
    
    private final List<GraphDataEventListener> listeners;
    
    public GraphDataEventListenerManagerImpl() {
        this.listeners = new CopyOnWriteArrayList<GraphDataEventListener>();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void addEventListener(GraphDataEventListener event) {
        if(event != null && !getListeners().contains(event)) {
            getListeners().add(event);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void removeEventListener(GraphDataEventListener event) {
        listeners.remove(event);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final synchronized void onCreate(GraphData data) {
        for(GraphDataEventListener listener : getListeners()) {
            listener.getHandler().onCreate(data);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final synchronized void onUpdate(GraphData oldData, GraphData newData) {
        for(GraphDataEventListener listener : getListeners()) {
            listener.getHandler().onUpdate(oldData, newData);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final synchronized void onDelete(GraphData data) {
        for(GraphDataEventListener listener : getListeners()) {
            listener.getHandler().onDelete(data);
        }
    }
    
    private List<GraphDataEventListener> getListeners() {
        return listeners;
    }
}
