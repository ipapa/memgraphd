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
import org.memgraphd.decision.Decision;
import org.memgraphd.decision.DecisionMaker;
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
    
    private final DecisionMaker decisionMaker;
    private final GraphDataEventListenerManager eventManager;
    private final GraphMappings mappings;
    private final GraphSeeker seeker;
    private final DataMatchmaker dataMatchmaker;
    
    public GraphWriterImpl(MemoryOperations memoryAccess, DecisionMaker decisionMaker, 
            GraphDataEventListenerManager eventManager, GraphMappings mappings, 
            GraphSeeker seeker, DataMatchmaker matchMaker) {
        super(memoryAccess);
        this.decisionMaker = decisionMaker;
        this.eventManager = eventManager;
        this.seeker = seeker;
        this.mappings = mappings;
        this.dataMatchmaker = matchMaker;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MemoryReference write(Data data) throws GraphException {
        // 1. Validate data first.
        validate(data);
        
        // 2. Send data to decision to get a sequence assigned to it
        Decision decision = decisionMaker.decideWriteRequest(data);
        
        // 3. Instantiate the graph data wrapper for this data.
        GraphData newData = new GraphDataImpl(decision);
        
        // 4. Is it an update or a new write request?
        // 4.1. Update relationships for data affected.
        // 4.2. Notify listeners about this change.
        MemoryReference ref = seeker.seekById(data.getId());
        if(ref != null) { // update
            GraphData oldData = getMemoryAccess().read(ref);
            getMemoryAccess().update(ref, newData);
            updateDataRelationships(oldData.getData(), data);
            eventManager.onUpdate(oldData, newData);
        }
        else { // new data
            ref = getMemoryAccess().write(newData);
            mappings.put(data.getId(), ref);
            buildDataRelationships(newData);
            eventManager.onCreate(newData);
        }
        
        // 5. Set the memory reference assigned to data on write (create|update)
        setMemoryReference(ref, newData);
        
        // 6. Update sequence mappings.
        mappings.put(decision.getSequence(), ref);
        
        LOGGER.info(String.format("Wrote data id=%s at memory reference=%d", data.getId(), ref.id()));
        return ref;
    }
    
    private void setMemoryReference(MemoryReference ref, GraphData data) {
        ((GraphDataImpl)data).setRefence(ref);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MemoryReference[] write(Data[] data) throws GraphException {
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
    
    private void validate(Data data) throws GraphException {
        
        if(data == null) {
            throw new GraphException("Data is null.");
        }
        
        if(data instanceof DataValidator) {
            if(!((DataValidator) data).isValid()) {
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
        mappings.delete(gData.getSequence());
        
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
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(String dataId) throws GraphException {
        MemoryReference ref = seeker.seekById(dataId);
        if(ref == null) {
            throw new GraphException(String.format("Data does not exist for id=%s", dataId));    
        }
        delete(getMemoryAccess().read(ref));
    }

}
