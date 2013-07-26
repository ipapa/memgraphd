package org.memgraphd.data.library.collection;

import java.util.ArrayList;
import java.util.List;

import org.memgraphd.data.GraphData;
import org.memgraphd.data.library.Category;
import org.memgraphd.data.library.DataPredicate;

/**
 * Default implementation of the {@link FilterableDataCollection} interface.
 * @author Ilirjan Papa
 * @since November 20, 2012
 *
 */
public class DefaultFilterableDataCollection implements FilterableDataCollection {
    
    private final List<GraphData> graphData;
    
    /**
     * Constructs a new instance.
     * @param graphData List of {@link GraphData} objects.
     */
    public DefaultFilterableDataCollection(List<GraphData> graphData) {
        this.graphData = graphData;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final LibraryDataCollection filterBy() {
        return new DefaultLibraryDataCollection(graphData);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final LibraryDataCollection filterBy(Category category) {
        List<GraphData> result = new ArrayList<GraphData>();
        for(GraphData gd : graphData) {
            if(category.getPredicate().apply(gd)) {
                result.add(gd);
            }
        }
        return new DefaultLibraryDataCollection(result);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final LibraryDataCollection filterByAll(Category[] categories) {
        List<GraphData> result = new ArrayList<GraphData>();
        for(GraphData gd : graphData) {
            boolean verdict = true;
            for(Category category : categories) {
                if(!category.getPredicate().apply(gd)) {
                    verdict = false;
                    break;
                }
            }
            if(verdict) {
                result.add(gd);
            }
        }
        return new DefaultLibraryDataCollection(result);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final LibraryDataCollection filterByAny(Category[] categories) {
        List<GraphData> result = new ArrayList<GraphData>();
        for(GraphData gd : graphData) {
            boolean verdict = false;
            for(Category category : categories) {
                if(category.getPredicate().apply(gd)) {
                    verdict = true;
                    break;
                }
            }
            if(verdict) {
                result.add(gd);
            }
        }
        return new DefaultLibraryDataCollection(result);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final LibraryDataCollection filterBy(DataPredicate predicate) {
        List<GraphData> result = new ArrayList<GraphData>();
        for(GraphData gd : graphData) {
            if(predicate.apply(gd)) {
                result.add(gd);
            }
        }
        return new DefaultLibraryDataCollection(result);
    }

}
