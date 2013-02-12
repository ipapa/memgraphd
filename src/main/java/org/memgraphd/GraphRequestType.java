package org.memgraphd;
/**
 * Captures CRUD operations on the {@link Graph} by its clients;
 * 
 * @author Ilirjan Papa
 * @since January 30, 2013
 *
 */
public enum GraphRequestType {
    /**
     * Create a new entry in the {@link Graph} - assuming no such entry exists.
     */
    CREATE,
    
    /**
     * Lookup an existing entry in the {@link Graph} - assuming such entry exists.
     */
    READ,
    
    /**
     * Update an existing entry in the {@link Graph} - assuming such entry exists.
     */
    UPDATE,
    
    /**
     * Delete an existing entry from the {@link Graph} - assuming such entry exists.
     */
    DELETE;
}
