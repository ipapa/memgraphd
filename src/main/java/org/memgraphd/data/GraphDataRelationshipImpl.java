package org.memgraphd.data;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Default implementation of {@link GraphDataRelationship}. 
 * @author Ilirjan Papa
 * @since August 28, 2012
 *
 */
public class GraphDataRelationshipImpl implements GraphDataRelationship {
    private final GraphData[] data;
    
    public GraphDataRelationshipImpl(GraphData[] data) {
        this.data = data;
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> Set<T> oneToMany(Class<T> className) {
        Set<T> set = new LinkedHashSet<T>();
        for(GraphData d : data) {
           if(d.getData().getClass().equals(className)) {
               set.add((T)d.getData());
           }
        }
        
        return set;
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T oneToOne(Class<T> className) {
        T result = null;
        for(GraphData d : data) {
            if(d.getData().getClass().equals(className)) {
                result = (T) d.getData();
                break;
            }
        }
        return result;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GraphData[] relationships() {
        return data;
    }

}
