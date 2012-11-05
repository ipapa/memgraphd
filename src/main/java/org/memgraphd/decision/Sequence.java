package org.memgraphd.decision;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.memgraphd.GraphLifecycleHandler;

/**
 * Each {@link Decision} that the {@link DecisionMaker} makes gets assigned a unique {@link Sequence} number.
 * 
 * @author Ilirjan Papa
 * @since July 12, 2012
 *
 */
public final class Sequence implements GraphLifecycleHandler {
    private final long number;
    private static Map<Long, Sequence> store = new HashMap<Long, Sequence>();
    
    private Sequence(long number) {
        this.number = number;
    }
    
    /**
     * Returns the sequence number.
     * @return sequence number as long
     */
    public final long number() {
        return number;
    }
    
    @Override
    public final int hashCode() {
        return (int) number;
    }
    
    @Override
    public final boolean equals(Object obj) {
        if(obj == null) return false;
        if(!obj.getClass().equals(getClass())) return false;
        return hashCode() == obj.hashCode();
    }
    
    public static final Sequence valueOf(long number) {
        if(store.containsKey(number)) {
            return store.get(number);
        }
        Sequence newSeq = new Sequence(number);
        store.put(number, newSeq);
        
        return newSeq;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void onStartup() {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void onClearAll() {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void onShutdown() {
        store.clear();
    }
    
    @Override
    public final String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
