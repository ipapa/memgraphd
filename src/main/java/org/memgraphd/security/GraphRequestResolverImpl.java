package org.memgraphd.security;

import org.apache.commons.lang.StringUtils;
import org.memgraphd.GraphRequestType;
import org.memgraphd.data.Data;
import org.memgraphd.data.GraphData;
import org.memgraphd.decision.Sequence;
import org.memgraphd.memory.MemoryReference;
import org.memgraphd.operation.GraphReader;

/**
 * Default implementation of {@link GraphRequestResolver}.
 * 
 * @author Ilirjan Papa
 * @since February 7, 2013
 *
 */
public class GraphRequestResolverImpl implements GraphRequestResolver {
    
    private final GraphReader reader;
    
    /**
     * Constructs a new instance.
     * @param reader {@link GraphReader}
     */
    public GraphRequestResolverImpl(GraphReader reader) {
        this.reader = reader;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GraphRequestContext resolve(GraphRequestType requestType, String dataId) {
        if(!StringUtils.isBlank(dataId)) {
            Data data = null;
            GraphData gData = reader.readId(dataId);
            if(GraphRequestType.DELETE.equals(requestType) && gData != null) {
                data = gData.getData();
            }
            return new GraphRequestContext(requestType, data, gData);
        }

        return new GraphRequestContext(requestType, null, null);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GraphRequestContext resolve(GraphRequestType requestType, Data data) {
        if(data != null) {
            return new GraphRequestContext(requestType, data, reader.readId(data.getId()));
        }
        else {
            return new GraphRequestContext(requestType, data, null);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GraphRequestContext resolve(GraphRequestType requestType, Sequence sequence) {
        if(sequence != null) {
            return new GraphRequestContext(requestType, null, reader.readSequence(sequence));
        }
        else {
            return new GraphRequestContext(requestType, null, null);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GraphRequestContext resolve(GraphRequestType requestType, MemoryReference reference) {
        if(reference != null) {
            return new GraphRequestContext(requestType, null, reader.readReference(reference));
        }
        else {
            return new GraphRequestContext(requestType, null, null);
        }
    }
    
}
