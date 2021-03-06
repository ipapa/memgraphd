package org.memgraphd.test.data;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.memgraphd.data.ReadWriteData;
import org.memgraphd.data.DataValidator;
import org.memgraphd.data.relationship.DataRelationship;



public class TvSeries extends ReadWriteData implements DataValidator, DataRelationship {

    /**
     *
     */
    private static final long serialVersionUID = -1983292094088692531L;

    private final String seriesName;
    private final String networkId;

    public TvSeries(String id, String networkId, DateTime createdDate, DateTime lastModifiedDate, String name) {
        super(id, createdDate, lastModifiedDate);
        this.seriesName = name;
        this.networkId = networkId;
    }

    @Override
    public boolean isValid() {
        return !StringUtils.isBlank(getId());
    }

    public final String getSeriesName() {
        return seriesName;
    }

    public final String getNetworkId() {
        return networkId;
    }

    @Override
    public String[] getRelatedIds() {
        return new String[] { getNetworkId() };
    }
}
