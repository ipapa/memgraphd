package org.memgraphd;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.memgraphd.data.GraphData;
import org.memgraphd.decision.Sequence;
import org.memgraphd.memory.MemoryReference;

/**
 * Default implementation of {@link GraphMappings} in charge of managing the state of all
 * memory reference mappings for {@link GraphData}.
 * 
 * @author Ilirjan Papa
 * @since July 27, 2012
 */
public final class GraphMappingsImpl implements GraphMappings {
    private final Map<String, MemoryReference> idMap;
    private final Map<Sequence, MemoryReference> seqMap;
    
    public GraphMappingsImpl() {
        this.idMap = new ConcurrentHashMap<String, MemoryReference>();
        this.seqMap = new ConcurrentHashMap<Sequence, MemoryReference>();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsId(String id) {
        return idMap.containsKey(id);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MemoryReference getById(String id) {
        return idMap.get(id);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void put(String id, MemoryReference ref) {
        idMap.put(id, ref);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsSequence(Sequence sequence) {
        return seqMap.containsKey(sequence);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MemoryReference getBySequence(Sequence sequence) {
        return seqMap.get(sequence);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void put(Sequence sequence, MemoryReference ref) {
        seqMap.put(sequence, ref);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(String id) {
        idMap.remove(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Sequence seq) {
        seqMap.remove(seq);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final Collection<MemoryReference> getAllMemoryReferences() {
        return  idMap.values();
    }

}
