package org.memgraphd.data.event;

import org.memgraphd.Graph;
import org.memgraphd.data.Data;
import org.memgraphd.data.GraphData;
/**
 * This interface should be implemented by all interested parties that want to listen in
 * for Graph data updates. You also need to register with {@link Graph#register(org.memgraphd.GraphLifecycleHandler)}.
 * 
 * @author Ilirjan Papa
 * @since September 12, 2012
 *
 */
public interface GraphDataEventHandler {
    
    /**
     * It will be called when a new piece of {@link Data} gets applies to {@link Graph}.
     * @param dataCreated {@link GraphData}
     */
    void onCreate(GraphData dataCreated);
    
    /**
     * It will be called when an existing piece of {@link Data} gets updated.
     * @param oldData a copy of old {@link GraphData}
     * @param newData a copy of new {@link GraphData} 
     */
    void onUpdate(GraphData oldData, GraphData newData);
    
    /**
     * It will be called when an existing piece of {@link Data} gets updated.
     * @param dataDeleted {@link GraphData}
     */
    void onDelete(GraphData dataDeleted);
}
