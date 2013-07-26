package org.memgraphd.decision;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.joda.time.DateTime;
import org.memgraphd.GraphRequestType;
import org.memgraphd.data.Data;

/**
 * Default implementation of {@link Decision}.
 * 
 * @author Ilirjan Papa
 * @since July 23, 2012
 *
 */
public final class DecisionImpl implements Decision {
    private final Sequence sequence;
    private final GraphRequestType requestType;
    private final String dataId;
    private final Data data;
    private final DateTime time;
    
    public DecisionImpl(Sequence seq, DateTime decionTime, GraphRequestType requestType, String dataId, Data data) {
        this.sequence = seq;
        this.time = decionTime;
        this.data = data;
        this.dataId = dataId;
        this.requestType = requestType;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final Sequence getSequence() {
        return sequence;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final DateTime getTime() {
        return time;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final GraphRequestType getRequestType() {
        return requestType;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final String getDataId() {
        return dataId;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final Data getData() {
        return data;
    }
    
    @Override
    public final String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
    
}
