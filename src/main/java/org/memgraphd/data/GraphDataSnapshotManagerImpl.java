package org.memgraphd.data;

import java.util.List;

import org.apache.log4j.Logger;
import org.memgraphd.Graph;
import org.memgraphd.GraphMappings;
import org.memgraphd.GraphRequestType;
import org.memgraphd.decision.Decision;
import org.memgraphd.decision.DecisionMaker;
import org.memgraphd.decision.Sequence;
import org.memgraphd.exception.GraphException;
import org.memgraphd.memory.MemoryReference;
import org.memgraphd.operation.GraphReader;
import org.memgraphd.operation.GraphStateManager;
import org.memgraphd.operation.GraphWriter;
/**
 * Its sole responsibility is to replay all decision stored in the book on application startup
 * so that the {@link Graph}'s state can be restored.
 * 
 * @author Ilirjan Papa
 * @since October 27, 2012
 *
 */
public class GraphDataSnapshotManagerImpl implements GraphDataSnapshotManager {
    private static final Logger LOGGER = Logger.getLogger(GraphDataSnapshotManagerImpl.class);
    
    private final GraphWriter writer;
    private final GraphStateManager stateManager;
    private final GraphReader reader;
    private final GraphMappings mappings;
    private final DecisionMaker decisionMaker;

    public GraphDataSnapshotManagerImpl(GraphReader reader, GraphWriter writer, GraphMappings mappings, 
            DecisionMaker decisionMaker, GraphStateManager stateManager) {
        this.reader = reader;
        this.mappings = mappings;
        this.writer = writer;
        this.stateManager = stateManager;
        this.decisionMaker = decisionMaker;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void initialize() {
        Sequence totalDecisions = decisionMaker.latestDecision();
        LOGGER.info(String.format("GraphDataInitializer is replaying %d decisions from disk", totalDecisions.number()));

        long iterations = totalDecisions.number() / decisionMaker.getReadWriteBatchSize();
        long remainder = totalDecisions.number() % decisionMaker.getReadWriteBatchSize();
        for(int i=0; i < iterations; i++) {
            Sequence start = Sequence.valueOf(i * decisionMaker.getReadWriteBatchSize());
            Sequence end = Sequence.valueOf(start.number() + decisionMaker.getReadWriteBatchSize());
            replayDecisionRange(start, end);
        }
        if(remainder > 0) {
            Sequence start = Sequence.valueOf(iterations * decisionMaker.getReadWriteBatchSize());
            Sequence end = Sequence.valueOf(start.number() + remainder);
            replayDecisionRange(start, end);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void clear() throws GraphException {
        LOGGER.info("Wiping out all graph data.");
        for (MemoryReference ref : mappings.getAllMemoryReferences()) {
            GraphData gData = reader.readReference(ref);
            try {
                LOGGER.info(String.format("Deleting graph data id=%s", gData.getData().getId()));
                writer.delete(gData.getData().getId());
            } catch (GraphException e) {
                LOGGER.error(
                        String.format("Failed to delete graph data id=%s", gData.getData().getId()),
                        e);
            }
        }
        // clear all enacted decisions as well.
        decisionMaker.reverseAll();

    }

    private void replayDecisionRange(Sequence start, Sequence end) {
        long startTime = System.currentTimeMillis();
        List<Decision> allDecsions = decisionMaker.readRange(start, end);
        LOGGER.info(String.format("Loaded %d decisions from disk in %d milliseconds.", allDecsions.size(), (System.currentTimeMillis() - startTime)));
        
        for(Decision d : allDecsions) {
            LOGGER.debug(String.format("Loading decision sequenceId=%d", d.getSequence().number()));
            try {
                if(GraphRequestType.DELETE == d.getRequestType()) {
                    stateManager.delete(d, reader.readId(d.getDataId()));
                }
                else if(GraphRequestType.CREATE == d.getRequestType())  {
                    stateManager.create(d);
                }
                else {
                    LOGGER.warn(String.format("Ignoring decision sequenceId=%d with bad request type=%s", d.getSequence().number(), d.getRequestType()));
                }
                
            } catch (GraphException e) {
                LOGGER.error("Failed to write decision to Graph " + d.toString());
            }
        }
    }

}
