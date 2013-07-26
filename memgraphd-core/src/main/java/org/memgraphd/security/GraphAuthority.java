package org.memgraphd.security;

import org.memgraphd.Graph;

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
     * It will throw a runtime exception if the request is not granted/authorized, meaning data permissions
     * do not allow this change to happen.
     * @param requestContext {@link GraphRequestContext}
     */
    void authorize(GraphRequestContext requestContext);

}
