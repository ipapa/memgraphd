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

    @Override
    public void onStartup() {
    }

    @Override
    public synchronized void onShutdown() {
        store.clear();
    }
    
    @Override
    public final String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
