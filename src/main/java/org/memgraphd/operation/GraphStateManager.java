package org.memgraphd.operation;

import org.memgraphd.Graph;
import org.memgraphd.data.GraphData;
import org.memgraphd.decision.Decision;
import org.memgraphd.exception.GraphException;
import org.memgraphd.memory.MemoryReference;

public interface GraphStateManager {
    
    /**
     * It will write an already made decision. Meant to be used only when the
     * {@link Graph} restarts and replays the transaction log of previous decisions
     * so it can reach the previous state before the {@link Graph} was stopped.
     * 
     * @param decision {@link Decision}
     * @return {@link MemoryReference}
     */
    MemoryReference create(Decision decision) throws GraphException;
    
    /**
     * It will update an already made decision. Meant to be used only when the
     * {@link Graph} restarts and replays the transaction log of previous decisions
     * so it can reach the previous state before the {@link Graph} was stopped.
     * 
     * @param decision {@link Decision}
     * @param existingData {@link GraphData}
     * @return {@link MemoryReference}
     */
    MemoryReference update(Decision decision, GraphData existingData) throws GraphException;
    
    /**
     * It will handle a delete decision that has already been made.
     * It is meant to be used by {@link Graph} on startup to replay
     * a transaction log of old decisions.
     * @param decision {@link Decision}
     * @param existingData {@link GraphData}
     * @throws GraphException
     */
    void delete(Decision decision, GraphData existingData) throws GraphException;
    
    
}
