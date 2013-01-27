package org.memgraphd.data;

import org.joda.time.DateTime;

/**
 * Represents an immutable instance of {@link ExpiringData} with read-write permissions.
 * @author Ilirjan Papa
 * @since January 27, 2013
 *
 */
public class ReadWriteExpiringData extends AbstractExpiringData {

    private static final long serialVersionUID = -1068295135914266484L;
    
    /**
     * Constructs a new immutable instance of {@link ExpiringData} with read-write permissions.
     * @param id
     * @param createdDate
     * @param expirationDate
     */
    public ReadWriteExpiringData(String id, DateTime createdDate, DateTime expirationDate) {
        this.id = id;
        this.createdDate = createdDate;
        this.expirationDate = expirationDate;
        this.read = true;
        this.write = true;
        this.update = true;
        this.delete = true;
    }
}
