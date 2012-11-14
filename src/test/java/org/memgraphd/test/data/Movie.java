package org.memgraphd.test.data;

import org.joda.time.DateTime;
import org.memgraphd.data.DataImpl;
import org.memgraphd.data.relationship.DataRelationship;

public class Movie extends DataImpl implements DataRelationship {
    private final String name;
    private final String networkId;
    
    public Movie(String id, String name, String networkId, DateTime createdDate, DateTime lastModifiedDate) {
        super(id, createdDate, lastModifiedDate);
        this.name = name;
        this.networkId = networkId;
    }

    public final String getName() {
        return name;
    }

    public final String getNetworkId() {
        return networkId;
    }

    /**
     * 
     */
    private static final long serialVersionUID = -615780058323486327L;

    @Override
    public String[] getRelatedIds() {
        return new String[] { getNetworkId() };
    }

}
