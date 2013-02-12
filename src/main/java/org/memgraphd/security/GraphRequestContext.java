package org.memgraphd.security;

import org.memgraphd.GraphRequestType;
import org.memgraphd.data.Data;
import org.memgraphd.data.GraphData;

public class GraphRequestContext {
    
    private final GraphRequestType requestType;
    
    private final Data data;
    
    private final GraphData graphData;
    
    public GraphRequestContext(GraphRequestType requestType, Data data, GraphData graphData) {
        this.requestType = requestType;
        this.data = data;
        this.graphData = graphData;
    }

    public final GraphRequestType getRequestType() {
        return requestType;
    }

    public final Data getData() {
        return data;
    }

    public final GraphData getGraphData() {
        return graphData;
    }
}
