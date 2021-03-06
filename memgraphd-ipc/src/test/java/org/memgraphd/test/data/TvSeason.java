package org.memgraphd.test.data;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.memgraphd.data.ReadWriteData;
import org.memgraphd.data.DataValidator;
import org.memgraphd.data.relationship.DataRelationship;



public class TvSeason extends ReadWriteData implements DataRelationship, DataValidator {

    /**
     *
     */
    private static final long serialVersionUID = 7016168350866382062L;

    private final String seasonNumber;
    private final String seriesId;

    public TvSeason(String id, DateTime createdDate, DateTime lastModifiedDate, String seasonNumber, String seriesId) {
        super(id, createdDate, lastModifiedDate);
        this.seasonNumber = seasonNumber;
        this.seriesId = seriesId;
    }

    @Override
    public boolean isValid() {
        return !StringUtils.isBlank(getId()) && !StringUtils.isBlank(getSeriesId());
    }

    @Override
    public String[] getRelatedIds() {
        if(!StringUtils.isBlank(getSeriesId())) {
            return new String[] { getSeriesId() };
        }
        return new String[] {};
    }

    public final String getSeasonNumber() {
        return seasonNumber;
    }

    public final String getSeriesId() {
        return seriesId;
    }

}
