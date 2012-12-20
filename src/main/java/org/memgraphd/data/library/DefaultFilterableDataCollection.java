package org.memgraphd.data.library;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.memgraphd.data.GraphData;

/**
 * Default implementation of the {@link FilterableDataCollection} interface.
 * @author Ilirjan Papa
 * @since November 20, 2012
 *
 */
public class DefaultFilterableDataCollection implements FilterableDataCollection {
    
    private final GraphData[] graphData;
    
    public DefaultFilterableDataCollection(GraphData[] graphData) {
        this.graphData = graphData;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final SortableDataCollection filterBy() {
        return new DefaultSortableDataCollection(Arrays.asList(graphData));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final SortableDataCollection filterBy(Category category) {
        List<GraphData> result = new ArrayList<GraphData>();
        for(GraphData gd : graphData) {
            if(category.getPredicate().apply(gd)) {
                result.add(gd);
            }
        }
        return new DefaultSortableDataCollection(result);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final SortableDataCollection filterByAll(Category[] categories) {
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
        return new DefaultSortableDataCollection(result);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final SortableDataCollection filterByAny(Category[] categories) {
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
        return new DefaultSortableDataCollection(result);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final SortableDataCollection filterBy(DataPredicate predicate) {
        List<GraphData> result = new ArrayList<GraphData>();
        for(GraphData gd : graphData) {
            if(predicate.apply(gd)) {
                result.add(gd);
            }
        }
        return new DefaultSortableDataCollection(result);
    }

}
