package org.memgraphd.memory;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.memgraphd.GraphLifecycleHandler;

/**
 * A memory reference is a unique identifier for a {@link MemoryLocation}.
 * 
 * @author Ilirjan Papa
 * @since July 12, 2012
 *
 */
public final class MemoryReference implements GraphLifecycleHandler {
    private final int id;
    private static Map<Integer, MemoryReference> store = new HashMap<Integer, MemoryReference>();
    
    private MemoryReference(int id) {
        this.id = id;
    }
    
    @Override
    public int hashCode() {
        return id;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        if(!obj.getClass().equals(MemoryReference.class)) 
            return false;
        return hashCode() == obj.hashCode();
    }

    public final int id() {
        return id;
    }
    
    /**
     * It generates an immutable instance of a {@link MemoryReference}.
     * @param id integer
     * @return {@link MemoryReference}
     */
    public static MemoryReference valueOf(int id) {
  
        if(store.containsKey(id)) {
            return store.get(id);
        }
        MemoryReference newImpl = new MemoryReference(id);
        store.put(id, newImpl);
        return newImpl;
    }
    
    /**
     * Read a range of {@link MemoryReference}(s) from start to end.
     * @param start integer
     * @param end integer
     * @return array of {@link MemoryReference}(s)
     */
    public static MemoryReference[] rangeOf(int start, int end) {
        if(end < start) {
            throw new RuntimeException("Invalid range specified: end < start");
        }
        MemoryReference[] range = new MemoryReference[end - start + 1];
        int count = 0;
        for(int i= start; i <= end; i++) {
            range[count] = valueOf(i);
            count++;
        }
        return range;
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
    public synchronized void onShutdown() {
        store.clear();
    }
    
    @Override
    public final String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
    
}
