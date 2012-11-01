package org.memgraphd.decision;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.joda.time.DateTime;
import org.memgraphd.request.GraphRequest;

/**
 * Default implementation of {@link Decision}.
 * 
 * @author Ilirjan Papa
 * @since July 23, 2012
 *
 */
public final class DecisionImpl implements Decision {
    private final Sequence sequence;
    private final GraphRequest request;
    private final DateTime time;
    
    public DecisionImpl(GraphRequest request, Sequence seq, DateTime time) {
        this.request = request;
        this.sequence = seq;
        this.time = time;
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
    public final GraphRequest getRequest() {
        return request;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final DateTime getTime() {
        return time;
    }
    
    @Override
    public final String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
