package org.memgraphd.test.data;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.memgraphd.data.ReadWriteData;
import org.memgraphd.data.DataValidator;
import org.memgraphd.data.relationship.DataRelationship;

public class OnlineVideo extends ReadWriteData implements DataRelationship, DataValidator {

    /**
     *
     */
    private static final long serialVersionUID = -3658283442147491751L;

    private final String title;
    private final String episodeId;
    private final boolean isLongForm;

    public OnlineVideo(String id, DateTime createdDate, DateTime lastModifiedDate, String title,
            String episodeId, boolean isLongForm) {
        super(id, createdDate, lastModifiedDate);
        this.title = title;
        this.episodeId = episodeId;
        this.isLongForm = isLongForm;
    }

    public final String getTitle() {
        return title;
    }

    public final String getEpisodeId() {
        return episodeId;
    }

    public final boolean isLongForm() {
        return isLongForm;
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
