package org.memgraphd.data;

import org.memgraphd.decision.Decision;
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
    private final Data data;
    private final GraphRelatedData relatedData;
    
    private MemoryReference reference;
    
    public GraphDataImpl(Decision decision, Data data) {
        this.decision = decision;
        this.data = data;
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
    public final Decision getDecision() {
        return decision;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final Data getData() {
        return data;
    }
    
    /**
     * {@inheritDoc}
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
}
