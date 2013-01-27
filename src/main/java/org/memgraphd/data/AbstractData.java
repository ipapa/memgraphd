package org.memgraphd.data;

import org.joda.time.DateTime;
/**
 * All implementations of {@link Data} interface should extend this abstract class.
 * @author Ilirjan Papa
 * @since January 27, 2013
 *
 */
public abstract class AbstractData extends AbstractDataPermissions implements Data {

    private static final long serialVersionUID = -7278305016297146334L;
    
    protected String id;
    protected DateTime createdDate;
    protected DateTime lastModifiedDate;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final String getId() {
        return id;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final DateTime getCreatedDate() {
        return createdDate;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final DateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

}
