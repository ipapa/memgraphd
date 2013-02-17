package org.memgraphd.operation;

import org.apache.log4j.Logger;
import org.memgraphd.GraphMappings;
import org.memgraphd.data.Data;
import org.memgraphd.data.GraphData;
import org.memgraphd.data.GraphDataImpl;
import org.memgraphd.data.event.GraphDataEventListenerManager;
import org.memgraphd.data.library.Librarian;
import org.memgraphd.data.relationship.DataMatchmaker;
import org.memgraphd.data.relationship.DataRelationship;
import org.memgraphd.decision.Decision;
import org.memgraphd.exception.GraphException;
import org.memgraphd.memory.MemoryReference;
import org.memgraphd.memory.operation.MemoryOperations;

import com.sun.corba.se.impl.orbutil.graph.Graph;
/**
 * And implementation of {@link GraphStateManager} that manages change request to the
 * state of the {@link Graph} data.
 * @author Ilirjan Papa
 * @since February 9, 2013
 *
 */
public class GraphStateManagerImpl extends AbstractGraphAccess implements GraphStateManager {
    private static final Logger LOGGER = Logger.getLogger(GraphStateManagerImpl.class);
    
    private final DataMatchmaker dataMatchmaker;
    
    private final GraphMappings mappings;
    
    private final Librarian librarian;
    
    private GraphDataEventListenerManager eventManager;
    
    /**
     * Constructs a new instance.
     * @param operations {@link MemoryOperations}
     * @param mapping {@link GraphMappings}
     * @param librarian {@link Librarian}
     * @param dataMatchmaker {@link DataMatchmaker}
     * @param eventManager {@link GraphDataEventListenerManager}
     */
    public GraphStateManagerImpl(MemoryOperations operations, GraphMappings mapping, 
            Librarian librarian, DataMatchmaker dataMatchmaker, GraphDataEventListenerManager eventManager) {
        super(operations);
        this.dataMatchmaker = dataMatchmaker;
        this.mappings = mapping;
        this.librarian = librarian;
        this.eventManager = eventManager;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MemoryReference create(Decision decision) throws GraphException {
        // 1. Instantiate the graph data wrapper for this data.
        GraphDataImpl newData = new GraphDataImpl(decision);
        
        // 2. Update data snapshot
        MemoryReference ref = handleInsert(decision.getData(), newData);
        
        LOGGER.info(String.format("Wrote data id=%s at memory reference=%d", decision.getData().getId(), ref.id()));
        
        // 3. Set the memory reference assigned to data on write (create|update)
        newData.setRefence(ref);
        
        // 4. Update sequence mappings.
        mappings.put(decision.getSequence(), ref);
        
        // 5. Let librarian know
        librarian.archive(newData);
        
        return ref;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MemoryReference update(Decision decision, GraphData graphData) throws GraphException {
        // 1. Instantiate the graph data wrapper for this data.
        GraphDataImpl newData = new GraphDataImpl(decision);
        
        // 2. Set the memory reference assigned to data on write (create|update)
        newData.setRefence(graphData.getReference());
        
        // 3. Update data snapshot
        handleUpdate(decision.getData(), newData, graphData);
        
        // 4. Update sequence mappings.
        mappings.put(decision.getSequence(), graphData.getReference());
        
        // 5. Let librarian know
        librarian.archive(newData);
        
        LOGGER.info(String.format("Wrote data id=%s at memory reference=%d", decision.getData().getId(), newData.getReference().id()));
        
        return newData.getReference();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Decision decision, GraphData gData) throws GraphException {
        Data data = decision.getData();
        
        LOGGER.info(String.format("Deleting data id=%s at memory reference=%d", decision.getDataId(), gData.getReference().id()));
        
        // 1. Update relationships
        handleDataRelationships(gData, data);
        
        // 2. Free the actual memory
        getMemoryAccess().free(gData.getReference());
        
        // 3. Clear old mappings
        mappings.delete(decision.getDataId());
        mappings.delete(gData.getSequence());
        
        // 4. Alert librarian
        librarian.unarchive(gData);
        
        // 5. Alert listeners
        eventManager.onDelete(gData);
    }

    private void handleDataRelationships(GraphData gData, Data data) {
        if(data instanceof DataRelationship) {
            DataRelationship relationships = (DataRelationship) data;         
            // separate existing relationships
            dataMatchmaker.separate(relationships);
        }
        else { // it might be simple Data and we need to clean up lingering references.
            getMemoryAccess().dereferenceAll(gData.getReference());
        }
    }
    
    private MemoryReference handleInsert(Data data, GraphData newData) {
        MemoryReference ref;
        ref = getMemoryAccess().write(newData);
        mappings.put(data.getId(), ref);
        buildDataRelationships(newData);
        eventManager.onCreate(newData);
        return ref;
    }
    
    private void handleUpdate(Data data, GraphData newData, GraphData oldData) {
        getMemoryAccess().update(oldData.getReference(), newData);
        updateDataRelationships(oldData.getData(), data);
        eventManager.onUpdate(oldData, newData);
    }
    
    private void buildDataRelationships(GraphData data) {
        if(data.getData() instanceof DataRelationship) {
            dataMatchmaker.match((DataRelationship) data.getData());
        }
        else {
            dataMatchmaker.bachelor(data.getData());
        }
    }
    
    private void updateDataRelationships(Data oldData, Data newData) {
        if(oldData instanceof DataRelationship && newData instanceof DataRelationship) {
            dataMatchmaker.revow((DataRelationship)oldData, (DataRelationship)newData);
        }
        else if(oldData instanceof DataRelationship) {
            dataMatchmaker.separate((DataRelationship)oldData);
            dataMatchmaker.bachelor(newData);
        }
        else if(newData instanceof DataRelationship) {
            dataMatchmaker.match((DataRelationship)newData);
        }
        else {
            dataMatchmaker.bachelor(newData);
        }
    }
}
