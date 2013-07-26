package org.memgraphd.data;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.memgraphd.decision.Decision;
import org.memgraphd.decision.Sequence;
import org.memgraphd.memory.MemoryReference;

/**
 * Default implementation of {@link GraphData}.
 * 
 * @author Ilirjan Papa
 * @since September 4, 2012
 *
 */
public class GraphDataImpl implements GraphData {
    private final Decision decision;
    private final GraphRelatedData relatedData;
    
    private MemoryReference reference;
    
    public GraphDataImpl(Decision decision) {
        this.decision = decision;
        this.relatedData = new GraphRelatedDataImpl();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final MemoryReference getReference() {
        return reference;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final Data getData() {
        return decision.getData();
    }
    
    /**
     * Set the memory reference assigned to this {@link GraphData}.
     * @param ref {@link MemoryReference}
     */
    public final synchronized void setRefence(MemoryReference ref) {
        this.reference = ref;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final GraphRelatedData getRelatedData() {
        return relatedData;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final Sequence getSequence() {
        return decision.getSequence();
    }
    
    @Override
    public final String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
