package org.memgraphd.data;

import org.joda.time.DateTime;

/**
 * An immutable base implementation of {@link Data} interface. Feel free to extend this.
 *
 * @author Ilirjan Papa
 * @since June 11, 2012
 *
 */
public class DataImpl implements Data {
    /**
     * 
     */
    private static final long serialVersionUID = 4198986927144007897L;
    
    private final String id;
    private final DateTime createdDate;
    private final DateTime lastModifiedDate;
    
    public DataImpl(String id, DateTime createdDate, DateTime lastModifiedDate) {
        this.id = id;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
    }
    
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
