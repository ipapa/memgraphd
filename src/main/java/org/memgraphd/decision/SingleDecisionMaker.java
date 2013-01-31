package org.memgraphd.decision;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.memgraphd.GraphRequestType;
import org.memgraphd.bookkeeper.BookKeeper;
import org.memgraphd.data.Data;

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
     */
    @Override
    public final Decision decidePutRequest(Data data) {
        Decision decision = new DecisionImpl(Sequence.valueOf(latestInUseSequence.incrementAndGet()), 
                new DateTime(), GraphRequestType.CREATE, data.getId(), data);
        LOGGER.info(String.format("Write decision: Assigned dataId=%s sequence=%d", data.getId(), decision.getSequence().number()));
        bookKeeper.record(decision);
        return decision;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final Decision decideDeleteRequest(Data data) {
        Decision decision = new DecisionImpl(Sequence.valueOf(latestInUseSequence.incrementAndGet()), 
                new DateTime(), GraphRequestType.DELETE, data.getId(), data);
        LOGGER.info(String.format("Delete decision: Assigned dataId=%s sequence=%d", data.getId(), decision.getSequence().number()));
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
    
    @Override
    public final synchronized void reverseAll() {
        LOGGER.info("Wiping out ALL decisions.");
        bookKeeper.wipeAll();
        Sequence seq = bookKeeper.lastTransactionId();
        LOGGER.info("Latest in-use decision sequence is: " + seq.number());
        latestInUseSequence.set(seq.number());
    }

    @Override
    public final long getReadWriteBatchSize() {
        return batchSize;
    }

}
