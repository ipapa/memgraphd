package org.memgraphd.data;

import org.joda.time.DateTime;

/**
 * Similar to {@link ReadOnlyData} with one big difference: you cannot delete instances of this type
 * but you can delete instances of {@link ReadOnlyData}.The idea here is that you can store data
 * in the Graph that you do not want to delete ever. Write once, read always, delete never.
 *
 * @author Ilirjan Papa
 * @since January 27, 2013
 *
 */
public class ReadOnlyProtectedData extends ReadOnlyData {

    /**
     * Constructs an immutable instance of {@link Data} with read-only permissions - no write or delete.
     * @param id {@link String}
     * @param createdDate {@link DateTime}
     */
    public ReadOnlyProtectedData(String id, DateTime createdDate) {
        super(id, createdDate);
        delete = false;
    }

}
