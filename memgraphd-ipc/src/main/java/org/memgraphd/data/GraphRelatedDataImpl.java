package org.memgraphd.data;

/**
 * Default implementation of {@link GraphRelatedData}.
 * 
 * @author Ilirjan Papa
 * @since August 28, 2012
 *
 */
public class GraphRelatedDataImpl implements GraphRelatedData {
    private GraphDataRelationship relationships;
    private GraphDataRelationship references;
    
    public GraphRelatedDataImpl() {}
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final GraphDataRelationship getLinks() {
        return relationships;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GraphDataRelationship getReferences() {
        return references;
    }
    
    /**
     * Set relationships for this instance of {@link GraphRelatedData}.
     * @param relationships {@link GraphDataRelationship}
     */
    public final synchronized void setRelationships(GraphDataRelationship relationships) {
        this.relationships = relationships;
    }
    
    /**
     * Set references for this instance of {@link GraphRelatedData}.
     * @param references {@link GraphDataRelationship}
     */
    public final synchronized void setReferences(GraphDataRelationship references) {
        this.references = references;
    }
    
}
