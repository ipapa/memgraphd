package org.memgraphd.data;

import java.io.Serializable;

import org.joda.time.DateTime;

/**
 * All data stored in memgraphd should implement this interface.
 * 
 * @author Ilirjan Papa
 * @since June. 11, 2012
 *
 */
public interface Data extends Serializable {
    /**
     * A unique identifier for this instance.
     * @return {@link String}
     */
    String getId();
    
    /**
     * The time this instance was first created.
     * @return {@link DateTime}
     */
    DateTime getCreatedDate();
    
    /**
     * The time this instance was last modified.
     * @return {@link DateTime}
     */
    DateTime getLastModifiedDate();
}
