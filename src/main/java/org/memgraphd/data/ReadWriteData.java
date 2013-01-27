package org.memgraphd.data;

import org.joda.time.DateTime;

/**
 * An immutable read-write implementation of {@link Data} interface. Feel free to extend this.
 *
 * @author Ilirjan Papa
 * @since June 11, 2012
 *
 */
public class ReadWriteData extends AbstractData {

    private static final long serialVersionUID = 4198986927144007897L;
    
    /**
     * Constructs an immutable instance of {@link Data} that is read-write.
     * @param id {@link String}
     * @param createdDate {@link DateTime}
     * @param lastModifiedDate {@link DateTime}
     */
    public ReadWriteData(String id, DateTime createdDate, DateTime lastModifiedDate) {
        this.id = id;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
        this.read = true;
        this.write = true;
        this.update = true;
        this.delete = true;
    }

}
