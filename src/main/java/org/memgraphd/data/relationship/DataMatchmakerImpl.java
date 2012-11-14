package org.memgraphd.data.relationship;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.memgraphd.data.Data;
import org.memgraphd.data.GraphData;
import org.memgraphd.memory.MemoryReference;
import org.memgraphd.memory.operation.MemoryOperations;
import org.memgraphd.operation.AbstractGraphAccess;
import org.memgraphd.operation.GraphSeeker;

/**
 * A basic implementation of {@link DataMatchmaker} which handles all data relationship needs.
 * 
 * @author Ilirjan Papa
 * @since July 23, 2012
 *
 */
public class DataMatchmakerImpl extends AbstractGraphAccess implements DataMatchmaker {
    
    private final Map<String, Set<MemoryReference>> singles;
    private final GraphSeeker seeker;
    
    public DataMatchmakerImpl(MemoryOperations memoryAccess, GraphSeeker seeker) {
        super(memoryAccess);
        this.seeker = seeker;
        this.singles = new ConcurrentHashMap<String, Set<MemoryReference>>();
    }

    @Override
    public void bachelor(Data data) {
        if (data != null && getSingles().containsKey(data.getId())) {
            String id = data.getId();
            MemoryReference ref = seeker.seekById(id);
            if (ref != null) {
                for (MemoryReference r : getSingles().get(id)) {
                    getMemoryAccess().link(r, ref);
                }
//                getSingles().remove(id);
            }
        }
    }
    
    @Override
    public void match(DataRelationship data) {
        if (data != null) {
            MemoryReference ref = seeker.seekById(data.getId());

            if (ref != null) {

                handleExistingSingles(ref, data);

                vow(data, ref);
            }
        }
    }

    public void vow(DataRelationship data, MemoryReference ref) {
        GraphData graphData = getMemoryAccess().read(ref);
        if (graphData != null) {
            String[] relatedIds = data.getRelatedIds();
            MemoryReference[] refs = seeker.seekById(relatedIds);
            for (int i = 0; i < refs.length; i++) {
                if (refs[i] == null) { // not found
                    updateSingles(relatedIds[i], ref);
                }
                else { // found
                    getMemoryAccess().link(ref, refs[i]);
                }
            }
        }
    }
    
    @Override
    public void revow(DataRelationship oldVows, DataRelationship newVows) {
        if(oldVows != null) {
            MemoryReference ref = seeker.seekById(oldVows.getId());
            
            getMemoryAccess().delink(ref, seeker.seekById(oldVows.getRelatedIds()));
            
            match(newVows);
        }
    }
    
    @Override
    public void separate(DataRelationship data) {
        if (data != null) {
            MemoryReference ref = seeker.seekById(data.getId());

            if (ref != null) {
                getMemoryAccess().delinkAll(ref);
                getMemoryAccess().dereferenceAll(ref);
            }
        }
    }
    
    private void handleExistingSingles(MemoryReference ref, DataRelationship data) {
        if (data != null && getSingles().containsKey(data.getId())) {
            if (ref != null) {
                for (MemoryReference r : getSingles().get(data.getId())) {
                    getMemoryAccess().link(r, ref);
                }
//                getSingles().remove(data.getId());
            }
        }
    }

    private void updateSingles(String id, MemoryReference ref) {
        if (!getSingles().containsKey(id)) {
            getSingles().put(id, new HashSet<MemoryReference>());
        }

        if (ref != null) {
            getSingles().get(id).add(ref);
        }
    }
    
    private Map<String, Set<MemoryReference>> getSingles() {
        return singles;
    }

}
