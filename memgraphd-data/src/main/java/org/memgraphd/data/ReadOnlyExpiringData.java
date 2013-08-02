package org.memgraphd.data;

import org.joda.time.DateTime;
/**
 * Represents an immutable instance of {@link ExpiringData} with read-only permissions.
 * @author Ilirjan Papa
 * @since January 27, 2013
 *
 */
public class ReadOnlyExpiringData extends AbstractExpiringData {

    /**
     * Constructs an immutable instance of {@link ExpiringData} with read-only permissions.
     * @param id {@link String}
     * @param createdDate {@link DateTime}
     * @param expirationDate {@link DateTime}
     */
    public ReadOnlyExpiringData(String id, DateTime createdDate, DateTime expirationDate) {
        this.id = id;
        this.createdDate = createdDate;
        this.expirationDate = expirationDate;
        this.read = true;
        this.write = true;
        this.update = false;
        this.delete = true;
    }

}
