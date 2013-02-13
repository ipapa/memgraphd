package org.memgraphd.security;

import org.memgraphd.GraphRequestType;
import org.memgraphd.data.Data;
import org.memgraphd.data.GraphData;
/**
 * A POJO that stores request context data: the request type, the data as input and
 * the existing {@link GraphData} representation of that {@link Data} in the Graph.
 * 
 * @author Ilirjan Papa
 * @since February 11, 2013
 *
 */
public class GraphRequestContext {
    
    private final GraphRequestType requestType;
    
    private final Data data;
    
    private final GraphData graphData;
    
    /**
     * Constructs new instance.
     * @param requestType {@link GraphRequestType}
     * @param data {@link Data}
     * @param graphData {@link GraphData}
     */
    public GraphRequestContext(GraphRequestType requestType, Data data, GraphData graphData) {
        this.requestType = requestType;
        this.data = data;
        this.graphData = graphData;
    }
    
    /**
     * Returns the type of request issued by caller.
     * @return {@link GraphRequestType}
     */
    public GraphRequestType getRequestType() {
        return requestType;
    }
    
    /**
     * Returns the {@link Data} provided by caller as input.
     * @return {@link Data}
     */
    public Data getData() {
        return data;
    }
    
    /**
     * Returns the {@link GraphData} representation of the data provided by caller in {@link #getData()},
     * if such data already exists in the {@link org.memgraphd.Graph}.
     * @return {@link GraphData}
     */
    public GraphData getGraphData() {
        return graphData;
    }
}
