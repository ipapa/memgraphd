package org.memgraphd.security;

import org.memgraphd.Graph;
import org.memgraphd.data.Data;

/**
 * The {@link GraphAuthority} is in charge of granting ore revoking requests that change the 
 * state of the {@link Graph}: write and delete requests. It will check both the permissions
 * of the existing {@link Graph} data and the permissions of the incoming new data that will replace
 * the data in the {@link Graph}. The other responsibility is to log grant/revoke requests to log.
 * 
 * @author Ilirjan Papa
 * @since January 29, 2013
 *
 */
public interface GraphAuthority {
    
    /**
     * It will throw a runtime exception if the delete request is not granted, meaning data permissions
     * do not allow this change to happen.
     * @param data {@link Data} data to be deleted in the {@link Graph}
     * @throws IllegalAccessException
     */
    void grantDelete(Data data);
    
    /**
     * It will throw a runtime exception if the insert request is not granted, meaning data permissions
     * do not allow this change to happen.
     * @param data {@link Data} data to be inserted in the {@link Graph}
     * @throws IllegalAccessException
     */
    void grantInsert(Data data);
    
    /**
     * It will throw a runtime exception if the update request is not granted, meaning data permissions do not
     * allow this change to happen. 
     * @param oldData {@link Data} existing data in the {@link Graph} that will be overwritten.
     * @param newData {@link Data} new data instance that will update the {@link Graph}.
     * @throws IllegalAccessException
     */
    void grantUpdate(Data oldData, Data newData);
}
