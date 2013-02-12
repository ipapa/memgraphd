package org.memgraphd.security;

import org.apache.log4j.Logger;
import org.memgraphd.data.Data;

/**
 * The default implementation of {@link GraphAuthority}.
 * 
 * @author Ilirjan Papa
 * @since January 29, 2013
 *
 */
public class GraphAuthorityImpl implements GraphAuthority {
    
    private static final Logger LOGGER = Logger.getLogger(GraphAuthorityImpl.class);
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void authorize(GraphRequestContext requestContext) {
        
        switch (requestContext.getRequestType()) {
            case CREATE:
                grantCreate(requestContext.getData());
                break;
            case UPDATE:
                grantUpdate(requestContext.getGraphData().getData(), requestContext.getData());
                break;
            case DELETE:
                grantDelete(requestContext.getGraphData().getData());
                break;
            default:
                logGrantedPermission(requestContext.getRequestType().name(), requestContext.getData().getId());
                break;
        }
        
    }

    private void grantDelete(Data data) {
        if(!data.canDelete()) {
            throwException(String.format("Data id=%s is protected and cannot be deleted.", data.getId()));
        }
        
        logGrantedPermission("delete", data.getId());
    }
    
    private void grantCreate(Data data) {
        if(!data.canWrite()) {
            throwException(String.format("Data id=%s permissions do not allow it to be stored.", data.getId()));
        }
        
        logGrantedPermission("insert", data.getId());
    }
    
    private void grantUpdate(Data oldData, Data newData) {
        if(!oldData.canUpdate()) {
            throwException(String.format("Existing data id=%s permissions do not allow an update.", oldData.getId()));
        }
        grantDelete(oldData);
        grantCreate(newData);
        
        logGrantedPermission("update", newData.getId());
    }
    
    private void logGrantedPermission(String requestType, String dataId) {
        LOGGER.debug(String.format("Granting %s request for data id=%s", requestType, dataId));
    }
    
    private void throwException(String message) {
        LOGGER.error(message);
        throw new RuntimeException(message);
    }

}
