package org.memgraphd.data;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.memgraphd.data.DataImpl;
import org.memgraphd.data.DataValidator;



public class TvSeries extends DataImpl implements DataValidator {
    private final String seriesName;
    
    public TvSeries(String id, DateTime createdDate, DateTime lastModifiedDate, String name) {
        super(id, createdDate, lastModifiedDate);
        this.seriesName = name;
    }

    @Override
    public boolean isValid() {
        return !StringUtils.isBlank(getId());
    }

    public final String getSeriesName() {
        return seriesName;
    }

}
