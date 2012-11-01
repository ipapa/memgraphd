package org.memgraphd.decision;

import org.joda.time.DateTime;
import org.memgraphd.request.GraphDeleteRequest;
import org.memgraphd.request.GraphRequest;
import org.memgraphd.request.GraphWriteRequest;

/**
 * The {@link DecisionMaker} makes {@link Decision}s on {@link GraphWriteRequest} and {@link GraphDeleteRequest}s events.
 * These events get ordered and assigned a {@link Sequence}. We also capture the {@link GraphRequest} the decision as based on
 * and the time that the decision was made.
 * 
 * @author Ilirjan Papa
 * @since July 23, 2012
 * 
 * @see DecisionMaker
 */
public interface Decision {
    
    /**
     * Returns the {@link Sequence} instance assigned to this decision.
     * @return {@link Sequence}
     */
    Sequence getSequence();
    
    /**
     * Returns the {@link GraphRequest} instance which was the input to this decision.
     * @return {@link GraphRequest} most likely a {@link GraphWriteRequest} or {@link GraphDeleteRequest}.
     */
    GraphRequest getRequest();
    
    /**
     * Returns the time the decision was made.
     * @return {@link DateTime}
     */
    DateTime getTime();

}
