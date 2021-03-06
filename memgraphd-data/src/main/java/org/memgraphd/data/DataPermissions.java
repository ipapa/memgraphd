package org.memgraphd.data;

import java.io.Serializable;


/**
 * A simple interface that captures all available permissions of {@link Data} instances in the Graph.
 * @author Ilirjan Papa
 * @since January 27, 2013
 *
 */
public interface DataPermissions extends Serializable {

    /**
     * Returns true if you have read access, otherwise returns false.
     * @return boolean
     */
    boolean canRead();

    /**
     * Returns true if you have write access, otherwise returns false.
     * @return boolean
     */
    boolean canWrite();

    /**
     * Returns true if you have access to update existing data, otherwise returns false.
     * This is similar to {@link #canWrite()} but not exactly the same thing. You might have access
     * to write data once in the Graph but you might want to restrict update access.
     * @return boolean
     */
    boolean canUpdate();

    /**
     * Returns true if you have delete access, otherwise returns false;
     * @return boolean
     */
    boolean canDelete();
}
