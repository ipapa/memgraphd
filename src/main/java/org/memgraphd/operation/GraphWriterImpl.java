package org.memgraphd.operation;

import org.apache.log4j.Logger;
import org.memgraphd.GraphMappings;
import org.memgraphd.data.Data;
import org.memgraphd.data.DataValidator;
import org.memgraphd.data.GraphData;
import org.memgraphd.data.GraphDataImpl;
import org.memgraphd.data.event.GraphDataEventListenerManager;
import org.memgraphd.data.relationship.DataMatchmaker;
import org.memgraphd.data.relationship.DataRelationship;
import org.memgraphd.exception.GraphException;
import org.memgraphd.memory.MemoryReference;
import org.memgraphd.memory.operation.MemoryOperations;
/**
 * Base implementation for {@link GraphWriter}.
 * 
 * @author Ilirjan Papa
 * @since August 4, 2012
 *
 */
public class GraphWriterImpl extends AbstractGraphAccess implements GraphWriter {
    private static final Logger LOGGER = Logger.getLogger(GraphWriterImpl.class);
    
    private final GraphDataEventListenerManager eventManager;
    private final GraphMappings mappings;
    private final GraphSeeker seeker;
    private final DataMatchmaker dataMatchmaker;
    
    public GraphWriterImpl(MemoryOperations memoryAccess, GraphDataEventListenerManager eventManager, 
            GraphMappings mappings, GraphSeeker seeker, DataMatchmaker matchMaker) {
        super(memoryAccess);
        this.eventManager = eventManager;
        this.seeker = seeker;
        this.mappings = mappings;
        this.dataMatchmaker = matchMaker;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MemoryReference write(GraphData data) throws GraphException {
        
        validate(data);
        
        MemoryReference ref = seeker.seekById(data.getData().getId());
        if(ref != null) {
            GraphData oldData = getMemoryAccess().read(ref);
            getMemoryAccess().update(ref, data);
            updateDataRelationships(oldData.getData(), data.getData());
            eventManager.onUpdate(oldData, data);
        }
        else {
            ref = getMemoryAccess().write(data);
            mappings.put(data.getData().getId(), ref);
            buildDataRelationships(data);
            eventManager.onCreate(data);
        }
        setMemoryReference(ref, data); // set reference on write (create|update)
        mappings.put(data.getDecision().getSequence(), ref);
        LOGGER.info(String.format("Wrote data id=%s at memory reference=%d", data.getData().getId(), ref.id()));
        return ref;
    }
    
    private void setMemoryReference(MemoryReference ref, GraphData data) {
        ((GraphDataImpl)data).setRefence(ref);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MemoryReference[] write(GraphData[] data) throws GraphException {
        MemoryReference[] result = new MemoryReference[data.length];
        for(int i=0; i < data.length; i++) {
            result[i] = write(data[i]);
        }
        return result;
    }
    
    private void buildDataRelationships(GraphData data) {
        if(data != null) {
            if(data.getData() instanceof DataRelationship) {
                dataMatchmaker.match((DataRelationship) data.getData());
            }
            else {
                dataMatchmaker.bachelor(data.getData());
            }
        }
    }
    
    private void updateDataRelationships(Data oldData, Data newData) {
        if(oldData instanceof DataRelationship && newData instanceof DataRelationship) {
            dataMatchmaker.revow((DataRelationship)oldData, (DataRelationship)newData);
        }
        else if(oldData instanceof DataRelationship && !(newData instanceof DataRelationship)) {
            dataMatchmaker.separate((DataRelationship)oldData);
            dataMatchmaker.bachelor(newData);
        }
        else if(!(oldData instanceof DataRelationship) && newData instanceof DataRelationship) {
            dataMatchmaker.match((DataRelationship)newData);
        }
        else {
            dataMatchmaker.bachelor(newData);
        }
    }
    
    private void validate(GraphData gData) throws GraphException {
        if(gData == null) {
            throw new GraphException("Graph data is null.");
        }
        
        if(gData.getData() == null) {
            throw new GraphException("Data is null.");
        }
        
        if(gData.getData() instanceof DataValidator) {
            if(!((DataValidator) gData.getData()).isValid()) {
                throw new GraphException("Data validation failed.");
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(GraphData gData) throws GraphException {
        Data data = gData.getData();
        if(data instanceof DataRelationship) {
            DataRelationship relationships = (DataRelationship) data;         
            // separate existing relationships
            dataMatchmaker.separate(relationships);
        }
        else { // it might be simple Data and we need to clean up lingering references.
            getMemoryAccess().dereferenceAll(gData.getReference());
        }
        // free the actual memory
        getMemoryAccess().free(gData.getReference());
        
        // clear old mappings
        mappings.delete(data.getId());
        mappings.delete(gData.getDecision().getSequence());
        
        // alert listeners
        eventManager.onDelete(gData);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(GraphData[] data) throws GraphException {
        for(GraphData sd : data) {
            delete(sd);
        }
    }

}
