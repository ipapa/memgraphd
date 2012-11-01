package org.memgraphd.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.memgraphd.Graph;
import org.memgraphd.data.Data;
import org.memgraphd.data.GraphData;
import org.memgraphd.data.GraphDataConverter;
import org.memgraphd.data.GraphDataImpl;
import org.memgraphd.decision.Decision;
import org.memgraphd.decision.DecisionMaker;
import org.memgraphd.decision.Sequence;
import org.memgraphd.exception.GraphException;
/**
 * Its sole responsibility is to replay all decision stored in the book on application startup
 * so that the {@link Graph}'s state can be restored.
 * 
 * @author Ilirjan Papa
 * @since October 27, 2012
 *
 */
public class GraphDataInitializer {
    private static final Logger LOGGER = Logger.getLogger(GraphDataInitializer.class);
    private static final long BATCH_READ_SIZE = 10000;
    
    private final Graph graph;
    private final DecisionMaker decisionMaker;
    private final GraphDataConverter converter;
    
    public GraphDataInitializer(Graph graph, DecisionMaker decisionMaker, GraphDataConverter converter) {
        this.graph = graph;
        this.decisionMaker = decisionMaker;
        this.converter = converter;
    }
    
    public void initialize() {
        Sequence zeroSequence = Sequence.valueOf(0);
        Sequence totalDecisions = decisionMaker.latestDecision();
        LOGGER.info(String.format("GraphDataInitializer is replaying %d decisions from disk", totalDecisions.number()));
        if(totalDecisions.number() <= BATCH_READ_SIZE) {
            replayDecisionRange(zeroSequence, totalDecisions);
        }
        else {
            long iterations = totalDecisions.number() / BATCH_READ_SIZE;
            long remainder = totalDecisions.number() % BATCH_READ_SIZE;
            for(int i=0; i < iterations; i++) {
                Sequence start = Sequence.valueOf(i * BATCH_READ_SIZE);
                Sequence end = Sequence.valueOf(start.number() + BATCH_READ_SIZE);
                replayDecisionRange(start, end);
            }
            if(remainder > 0) {
                Sequence start = Sequence.valueOf(iterations * BATCH_READ_SIZE);
                Sequence end = Sequence.valueOf(start.number() + remainder);
                replayDecisionRange(start, end);
            }
        }
    }
    
    private void replayDecisionRange(Sequence start, Sequence end) {
        long startTime = System.currentTimeMillis();
        List<Decision> allDecsions = decisionMaker.readRange(start, end);
        LOGGER.info(String.format("Loaded %d decisions from disk in %d milliseconds.", allDecsions.size(), (System.currentTimeMillis() - startTime)));
        
        for(Decision d : allDecsions) {
            LOGGER.debug(String.format("Loading decision sequenceId=%d", d.getSequence().number()));
            try {
                // 1. Read the data from request object
                Data data = converter.convert(d.getRequest());
                
                // 2. Construct graph data object
                GraphData graphData = new GraphDataImpl(d, data);
                
                // 3. Record this data in the brain
                graph.write(graphData);
                
            } catch (GraphException e) {
                LOGGER.error("Failed to write decision to Graph " + d.toString());
            }
        }
    }

}
