package org.memgraphd.security;

import org.memgraphd.GraphRequestType;
import org.memgraphd.data.Data;
import org.memgraphd.decision.Sequence;
import org.memgraphd.memory.MemoryReference;

/**
 * Graph resolver is in charge of resolving graph request for data by using
 * the request type and the input data provided by caller. It instantiates an
 * instance of {@link GraphRequestContext} which contains request data plus
 * related existing graph data for that input.
 * 
 * @author Ilirjan Papa
 * @since February 7, 2013
 *
 */
public interface GraphRequestResolver {
    
    /**
     * Resolves the request by data id as string.
     * @param requestType GraphRequestType
     * @param dataId {@link String}
     * @return {@link GraphRequestContext}
     */
    GraphRequestContext resolve(GraphRequestType requestType, String dataId);
    
    /**
     * Resolves the request by {@link Data}.
     * @param requestType GraphRequestType
     * @param data {@link Data}
     * @return {@link GraphRequestContext}
     */
    GraphRequestContext resolve(GraphRequestType requestType, Data data);
    
    /**
     * Resolves the request by {@link Sequence}.
     * @param requestType GraphRequestType
     * @param sequence {@link Sequence}
     * @return {@link GraphRequestContext}
     */
    GraphRequestContext resolve(GraphRequestType requestType, Sequence sequence);
    
    /**
     * Resolves the request by {@link MemoryReference}.
     * @param requestType GraphRequestType
     * @param reference {@link MemoryReference}
     * @return {@link GraphRequestContext}
     */
    GraphRequestContext resolve(GraphRequestType requestType, MemoryReference reference);
    
}
