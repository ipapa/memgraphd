package org.memgraphd.test.data;

import org.joda.time.DateTime;
import org.memgraphd.data.ReadWriteData;

public class TvNetwork extends ReadWriteData {

    /**
     * 
     */
    private static final long serialVersionUID = -644552113590998327L;
    
    private final String name;

    public TvNetwork(String id, String name, DateTime createdDate, DateTime lastModifiedDate) {
        super(id, createdDate, lastModifiedDate);
        this.name = name;
    }
    
    public final String getName() {
        return name;
    }
}
