package org.memgraphd.data;

import org.memgraphd.Graph;
import org.memgraphd.exception.GraphException;

/**
 * It is responsible for managing {@link Graph}'s data snapshot.
 * 
 * @author Ilirjan Papa
 * @since November 17, 2012
 *
 */
public interface GraphDataSnapshotManager {
    
    /**
     * It will initialize the {@link Graph} on startup with data from decisions in the transaction log.
     * @throws GraphException
     */
    void initialize() throws GraphException;
    
    /**
     * Wipes out all the data from the {@link Graph}.<br>
     * <b>WARNING: This will permanently wipe out all the data you have stored in this {@link Graph}.</b>
     * @throws GraphException 
     */
    void clear() throws GraphException;
}
