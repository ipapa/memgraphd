package org.memgraphd.data;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.memgraphd.data.DataImpl;
import org.memgraphd.data.DataValidator;
import org.memgraphd.data.relationship.DataRelationship;



public class OnlineVideo extends DataImpl implements DataRelationship, DataValidator {
    /**
     * 
     */
    private static final long serialVersionUID = 5747201262235067602L;
    
    private final String title;
    private final String episodeId;
    
    public OnlineVideo(String id, DateTime createdDate, DateTime lastModifiedDate, String title, String episodeId) {
        super(id, createdDate, lastModifiedDate);
        this.title = title;
        this.episodeId = episodeId;
    }

    public final String getTitle() {
        return title;
    }

    public final String getEpisodeId() {
        return episodeId;
    }

    @Override
    public boolean isValid() {
        return !StringUtils.isBlank(getId()) && !StringUtils.isBlank(episodeId);
    }

    @Override
    public String[] getRelatedIds() {
        if(!StringUtils.isBlank(getEpisodeId())) {
            return new String[] { getEpisodeId() };
        }
        return new String[] {};
    }

}
