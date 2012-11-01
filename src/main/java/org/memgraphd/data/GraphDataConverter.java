package org.memgraphd.data;

import org.memgraphd.exception.GraphException;
import org.memgraphd.request.GraphRequest;
/**
 * An interface that knows how to convert a {@link GraphRequest} into {@link Data} and the other way around.
 * Somewhat of a work-in-progress, have yet to figure out how this will work.
 * 
 * @author Ilirjan Papa
 * @since September 30, 2012
 *
 */
public interface GraphDataConverter {
    
    /**
     * Converts a request into {@link Data}.
     * @param request {@link GraphRequest}
     * @return {@link Data}
     * @throws GraphException
     */
    Data convert(GraphRequest request) throws GraphException;
    
    /**
     * Recreate the original request state from the {@link Data} instance.
     * @param data {@link Data}
     * @return {@link GraphRequest}
     * @throws GraphException
     */
    GraphRequest convert(Data data) throws GraphException;
    
}
