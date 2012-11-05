package org.memgraphd.decision;

import org.joda.time.DateTime;
import org.memgraphd.GraphRequestType;
import org.memgraphd.data.Data;

/**
 * The {@link DecisionMaker} makes {@link Decision}(s) on incoming Graph events that change the state of our data model.
 * These requests are either {@link GraphRequestType#PUT} or {@link GraphRequestType#DELETE}.
 * These events get ordered and assigned a {@link Sequence}. 
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
     * The type of request that got decided.
     * @return {@link GraphRequestType}
     */
    GraphRequestType getRequestType();
    
    /**
     * Returns the id of the data concerning this decision.
     * @return {@link String}
     */
    String getDataId();
    
    /**
     * The {@link Data} object on which we based our decision.
     * This only applies to write events, not deletes.
     * @return {@link Data}
     */
    Data getData();
    
    /**
     * Returns the time the decision was made.
     * @return {@link DateTime}
     */
    DateTime getTime();

}
