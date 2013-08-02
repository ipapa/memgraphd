package org.memgraphd.data;

/**
 * Instances of {@link Data} should implement this interface if they want
 * to tell memgraphd to validate the data on write events.
 * 
 * @author Ilirjan Papa
 * @since July 20, 2012
 */
public interface DataValidator {
    
    /**
     * Tell me if this instance is valid to be stored in memgraphd.
     * @return true if it is valid, false otherwise.
     */
    boolean isValid();
    
}
