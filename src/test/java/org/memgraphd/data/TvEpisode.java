package org.memgraphd.data;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.memgraphd.data.DataImpl;
import org.memgraphd.data.DataValidator;
import org.memgraphd.data.relationship.DataRelationship;



public class TvEpisode extends DataImpl implements DataValidator, DataRelationship {
    /**
     * 
     */
    private static final long serialVersionUID = -8362521491512221109L;
    
    private final String seasonId;
    private final String episodeName;
    private final String episodeNumber;
    private final DateTime episodeOriginalAirDate;
    
    public TvEpisode(String id, DateTime createdDate, DateTime lastModifiedDate, String seasonId,
            String episodeName, String episodeNumber, DateTime airDate) {
        super(id, createdDate, lastModifiedDate);
        this.seasonId = seasonId;
        this.episodeName = episodeName;
        this.episodeNumber = episodeNumber;
        this.episodeOriginalAirDate = airDate;
    }

    public final String getSeasonId() {
        return seasonId;
    }

    public final String getEpisodeName() {
        return episodeName;
    }

    public final String getEpisodeNumber() {
        return episodeNumber;
    }

    public final DateTime getEpisodeOriginalAirDate() {
        return episodeOriginalAirDate;
    }

    @Override
    public boolean isValid() {
        return !StringUtils.isBlank(getId()) && !StringUtils.isBlank(seasonId);
    }

    @Override
    public String[] getRelatedIds() {
        if(!StringUtils.isBlank(getSeasonId())) {
            return new String[] { getSeasonId() };
        }
        return new String[] {};
    }

}
