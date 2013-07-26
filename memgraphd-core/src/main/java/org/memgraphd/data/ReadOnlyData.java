package org.memgraphd.data;

import org.joda.time.DateTime;

import com.sun.corba.se.impl.orbutil.graph.Graph;

/**
 * Represents data that has read-only permissions: store once in the graph and never update in the future.
 * Trying to update the same data id in the graph with a new instance will cause a runtime exception.
 * You can only insert/write or delete this type of data in the {@link Graph} but not udpate it.
 * 
 * @author Ilirjan Papa
 * @since January 27, 2013
 *
 */
public class ReadOnlyData extends AbstractData {

    private static final long serialVersionUID = 3348398432260452491L;
    
    /**
     * Constructs an immutable instance of {@link Data} that has read-only permissions.
     * @param id {@link String}
     * @param createdDate {@link DateTime}
     */
    public ReadOnlyData(String id, DateTime createdDate) {
        this.id = id;
        this.createdDate = createdDate;
        this.read = true;
        this.write = true;
        this.update = false;
        this.delete = true;
    }
    
}
