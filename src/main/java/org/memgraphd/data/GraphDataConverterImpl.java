package org.memgraphd.data;

import org.joda.time.DateTime;
import org.memgraphd.exception.GraphException;
import org.memgraphd.request.GraphRequest;
/**
 * Unfinished implementation of a {@link GraphDataConverter}.
 * 
 * @author Ilirjan Papa
 * @since September 30, 2012
 *
 */
public class GraphDataConverterImpl implements GraphDataConverter {
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Data convert(GraphRequest request) throws GraphException {
        return new DataImpl(request.getRequestUri(), request.getTime(), new DateTime());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GraphRequest convert(Data data) throws GraphException {
        // TODO Auto-generated method stub
        return null;
    }

}
