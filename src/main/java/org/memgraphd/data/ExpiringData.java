package org.memgraphd.data;

import org.joda.time.DateTime;

/**
 * 
 * @author Ilirjan Papa
 * @since January 27, 2013
 *
 */
public interface ExpiringData extends Data {
    
    /**
     * Returns the date and time when this instance of {@link Data} expires.
     * @return {@link DateTime}
     */
    DateTime getExpirationDate();
    
    /**
     * Returns true if the data has expired, otherwise false.
     * @return boolean
     */
    boolean hasExpired();
    
}
