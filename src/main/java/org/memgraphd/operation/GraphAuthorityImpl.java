package org.memgraphd.operation;

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
    public void grantDelete(Data data) {
        if(!data.canDelete()) {
            throwException(String.format("Data id=%s is protected and cannot be deleted.", data.getId()));
        }
        
        logGrantedPermission("delete", data.getId());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void grantInsert(Data data) {
        if(!data.canWrite()) {
            throwException(String.format("Data id=%s permissions do not allow it to be stored.", data.getId()));
        }
        
        logGrantedPermission("insert", data.getId());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void grantUpdate(Data oldData, Data newData) {
        if(!oldData.canUpdate()) {
            throwException(String.format("Existing data id=%s permissions do not allow an update.", oldData.getId()));
        }
        grantDelete(oldData);
        grantInsert(newData);
        
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
