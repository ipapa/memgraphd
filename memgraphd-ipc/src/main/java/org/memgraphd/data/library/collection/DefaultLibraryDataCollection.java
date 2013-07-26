package org.memgraphd.data.library.collection;

import java.util.ArrayList;
import java.util.List;

import org.memgraphd.data.GraphData;
import org.memgraphd.data.comparator.SortOrder;
import org.memgraphd.data.library.Category;
import org.memgraphd.data.library.DataPredicate;
/**
 * Default implementation of {@link LibraryDataCollection} interface.
 * @author Ilirjan Papa
 * @since January 2, 2013
 *
 */
public class DefaultLibraryDataCollection implements LibraryDataCollection {
    private final SortableDataCollection sortable;
    private final FilterableDataCollection filterable;
    private final List<GraphData> collection;
    
    /**
     * Use this immutable instance of an empty {@link LibraryDataCollection}.
     */
    public static final LibraryDataCollection EMPTY = 
            new DefaultLibraryDataCollection(new ArrayList<GraphData>());
    
    /**
     * Constructs a new instance of {@link DefaultLibraryDataCollection}.
     * @param collection {@link List} of {@link GraphData}.
     */
    public DefaultLibraryDataCollection(List<GraphData> collection) {
        this.sortable = new DefaultSortableDataCollection(collection);
        this.filterable = new DefaultFilterableDataCollection(collection);
        this.collection = collection;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final LibraryDataCollection sortById(SortOrder order) {
        return sortable.sortById(order);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final LibraryDataCollection sortByCreatedDate(SortOrder order) {
        return sortable.sortByCreatedDate(order);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final LibraryDataCollection sortByLastModifiedDate(SortOrder order) {
        return sortable.sortByLastModifiedDate(order);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final LibraryDataCollection filterBy() {
        return filterable.filterBy();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final LibraryDataCollection filterBy(Category category) {
        return filterable.filterBy(category);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final LibraryDataCollection filterByAll(Category[] categories) {
        return filterable.filterByAll(categories);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final LibraryDataCollection filterByAny(Category[] categories) {
        return filterable.filterByAny(categories);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final LibraryDataCollection filterBy(DataPredicate predicate) {
        return filterable.filterBy(predicate);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final int size() {
        return collection.size();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean contains(GraphData gData) {
        return collection.contains(gData);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final GraphData[] list() {
        return collection.toArray(new GraphData[] {});
    }

}
