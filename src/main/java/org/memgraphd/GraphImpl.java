package org.memgraphd;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.memgraphd.controller.GraphController;
import org.memgraphd.data.GraphData;
import org.memgraphd.data.event.GraphDataEventListener;
import org.memgraphd.data.event.GraphDataEventListenerManagerImpl;
import org.memgraphd.data.relationship.DataMatchmaker;
import org.memgraphd.data.relationship.DataMatchmakerImpl;
import org.memgraphd.decision.Decision;
import org.memgraphd.decision.Sequence;
import org.memgraphd.exception.GraphException;
import org.memgraphd.memory.MemoryAccess;
import org.memgraphd.memory.MemoryBlock;
import org.memgraphd.memory.MemoryManager;
import org.memgraphd.memory.MemoryReference;
import org.memgraphd.memory.MemoryStats;
import org.memgraphd.memory.operation.MemoryOperations;
import org.memgraphd.operation.GraphFilter;
import org.memgraphd.operation.GraphFilterImpl;
import org.memgraphd.operation.GraphReader;
import org.memgraphd.operation.GraphReaderImpl;
import org.memgraphd.operation.GraphSeeker;
import org.memgraphd.operation.GraphSeekerImpl;
import org.memgraphd.operation.GraphWriter;
import org.memgraphd.operation.GraphWriterImpl;

/**
 * This is the default implementation of {@link Graph} that brings all functionality together.
 * It is not meant to be accessed directly but through the {@link GraphController}.
 * 
 * @author Ilirjan Papa
 * @since August 17, 2012
 *
 */
public final class GraphImpl extends GraphSupervisorImpl implements Graph {
    
    private static final Logger LOGGER = Logger.getLogger(GraphImpl.class);
    
    private final GraphMappings mappings;
    private final GraphFilter filter;
    private final GraphReader reader;
    private final GraphWriter writer;
    private final GraphSeeker seeker;
    
    private final MemoryOperations memoryAccess;
    private final DataMatchmaker dataMatchmaker;
    
    public GraphImpl(MemoryManager memoryManager) {
        this.mappings = new GraphMappingsImpl();
        
        this.memoryAccess = new MemoryAccess(memoryManager);
        this.seeker = new GraphSeekerImpl(memoryAccess, mappings);
        this.reader = new GraphReaderImpl(memoryAccess, seeker);
        this.dataMatchmaker = new DataMatchmakerImpl(memoryAccess, seeker);
        this.writer = new GraphWriterImpl(memoryAccess, 
                new GraphDataEventListenerManagerImpl(new ArrayList<GraphDataEventListener>()), 
                mappings, seeker, dataMatchmaker);
        this.filter = new GraphFilterImpl(memoryAccess);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GraphData readId(String id) {
        authorize();
        return reader.readId(id);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GraphData readSequence(Sequence seq) {
        authorize();
        return reader.readSequence(seq);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GraphData readReference(MemoryReference ref) {
        authorize();
        return reader.readReference(ref);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GraphData[] readIds(String[] ids) {
        authorize();
        return reader.readIds(ids);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GraphData[] readSequences(Sequence[] seqs) {
        authorize();
        return reader.readSequences(seqs);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GraphData[] readReferences(MemoryReference[] refs) {
        authorize();
        return reader.readReferences(refs);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MemoryReference write(Decision decision) throws GraphException {
        authorize();
        return writer.write(decision);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MemoryReference[] write(Decision[] decisions) throws GraphException {
        authorize();
        return writer.write(decisions);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(GraphData data) throws GraphException {
        authorize();
        writer.delete(data);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(GraphData[] data) throws GraphException {
        authorize();
        writer.delete(data);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MemoryReference seekById(String id) {
        authorize();
        return seeker.seekById(id);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MemoryReference seekBySequence(Sequence seq) {
        authorize();
        return seeker.seekBySequence(seq);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MemoryReference[] seekById(String[] ids) {
        authorize();
        return seeker.seekById(ids);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MemoryReference[] seekBySequence(Sequence[] seqs) {
        authorize();
        return seeker.seekBySequence(seqs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsId(String id) {
        authorize();
        return mappings.containsId(id);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MemoryReference getById(String id) {
        authorize();
        return mappings.getById(id);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void put(String id, MemoryReference ref) {
        authorize();
        mappings.put(id, ref);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(String id) {
        authorize();
        mappings.delete(id);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Sequence seq) {
        authorize();
        mappings.delete(seq);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsSequence(Sequence sequence) {
        authorize();
        return mappings.containsSequence(sequence);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MemoryReference getBySequence(Sequence sequence) {
        authorize();
        return mappings.getBySequence(sequence);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void put(Sequence sequence, MemoryReference ref) {
        authorize();
        mappings.put(sequence, ref);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GraphData[] filterBy(MemoryBlock block) {
        authorize();
        return filter.filterBy(block);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GraphData[] filterByRange(MemoryReference startRef, MemoryReference endRef) {
        authorize();
        return filterByRange(startRef, endRef);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GraphData[] filterByRange(Sequence startSeq, Sequence endSeq) {
        authorize();
        return filterByRange(startSeq, endSeq);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final synchronized void clear() {
        authorize();
        LOGGER.info("Wiping out all graph data.");
        for(MemoryReference ref : mappings.getAllMemoryReferences()) {
            GraphData gData = readReference(ref);
            try {
                LOGGER.info(String.format("Deleting graph data id=%s", gData.getData().getId()));
                delete(gData);
            } catch (GraphException e) {
                LOGGER.error(String.format("Failed to delete graph data id=%s", gData.getData().getId()), e);
            }
        }
        
        notifyOnClearAll();
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isEmpty() {
        authorize();
        return ((MemoryStats)memoryAccess).occupied() == 0;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final Collection<MemoryReference> getAllMemoryReferences() {
        return mappings.getAllMemoryReferences();
    }

}
