package org.memgraphd.data.library;

/**
 * Simple implementation of {@link Category} interface.
 * @author Ilirjan Papa
 * @since December 20, 2012
 *
 */
public class CategoryImpl implements Category {
    private final String name;
    private final DataPredicate predicate;
    
    /**
     * Construct a new instance of {@link CategoryImpl}.
     * @param name category name as {@link String}
     * @param predicate {@link DataPredicate}
     */
    public CategoryImpl(String name, DataPredicate predicate) {
        this.name = name;
        this.predicate = predicate;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final String getName() {
        return this.name;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final DataPredicate getPredicate() {
        return this.predicate;
    }

}
