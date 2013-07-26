package org.memgraphd.decision;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.memgraphd.GraphRequestType;
import org.memgraphd.bookkeeper.BookKeeper;
import org.memgraphd.exception.GraphException;
import org.memgraphd.security.GraphRequestContext;

/**
 * A single node implementation of a {@link DecisionMaker} meant to be used when running on a single JVM.
 * 
 * @author Ilirjan Papa
 * @since July 27, 2012
 *
 */
public class SingleDecisionMaker implements DecisionMaker {
    private final Logger LOGGER = Logger.getLogger(getClass());
    
    private final BookKeeper bookKeeper;
    private final AtomicLong latestInUseSequence;
    private final long batchSize;
    
    /**
     * Constructs a new instance of a {@link SingleDecisionMaker}.
     * @param bookKeeper {@link BookKeeper}
     * @param batchSize the size of data that goes into a batch read/write operation as long
     */
    public SingleDecisionMaker(BookKeeper bookKeeper, long batchSize) {
        this.bookKeeper = bookKeeper;
        this.latestInUseSequence = new AtomicLong();
        this.batchSize = batchSize;
    }
    
    /**
     * {@inheritDoc}
     * @throws GraphException 
     */
    @Override
    public Decision decide(GraphRequestContext context) throws GraphException {
        if(GraphRequestType.READ.equals(context.getRequestType())) {
            throw new GraphException("No decision needed for READ requests.");
        }
        Decision decision = new DecisionImpl(Sequence.valueOf(latestInUseSequence.incrementAndGet()), 
                new DateTime(), context.getRequestType(), context.getData().getId(), context.getData());
        LOGGER.info(String.format("%s decision: Assigned dataId=%s sequence=%d",
                    new Object[] { context.getRequestType(), context.getData().getId(), decision.getSequence().number()}));
        bookKeeper.record(decision);
        return decision;
    }
   
    /**
     * {@inheritDoc}
     */
    @Override
    public final List<Decision> readRange(Sequence start, Sequence end) {
        return bookKeeper.readRange(start, end);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final Sequence latestDecision() {
        Sequence seq = Sequence.valueOf(latestInUseSequence.get());
        LOGGER.info("Latest in-use decision sequence is: " + seq.number());
        return seq;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final synchronized void reverse(Decision decision) {
        LOGGER.info(String.format("Wiping out decision with sequence=%d", decision.getSequence().number()));
        bookKeeper.wipe(decision);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final synchronized void reverseAll() {
        LOGGER.info("Wiping out ALL decisions.");
        bookKeeper.wipeAll();
        Sequence seq = bookKeeper.lastTransactionId();
        LOGGER.info("Latest in-use decision sequence is: " + seq.number());
        latestInUseSequence.set(seq.number());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final long getReadWriteBatchSize() {
        return batchSize;
    }

}
