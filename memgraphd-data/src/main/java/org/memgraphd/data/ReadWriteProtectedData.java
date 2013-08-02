package org.memgraphd.data;

import org.joda.time.DateTime;

/**
 * Represents an instance of {@link Data} that has read-write access but no delete access. You can
 * write this data in the Graph once, update it as often as you like, but you cannot delete it ever.
 * @author Ilirjan Papa
 * @since January 27, 2012
 *
 */
public class ReadWriteProtectedData extends ReadWriteData {

    /**
     * Constructs a new immutable instance of {@link Data} with read-write access but no delete access.
     * @param id {@link String}
     * @param createdDate {@link DateTime}
     * @param lastModifiedDate {@link DateTime}
     */
    public ReadWriteProtectedData(String id, DateTime createdDate, DateTime lastModifiedDate) {
        super(id, createdDate, lastModifiedDate);
        delete = false;
    }

}
