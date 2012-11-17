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
    
    public SingleDecisionMaker(BookKeeper bookKeeper) {
        this.bookKeeper = bookKeeper;
        this.latestInUseSequence = new AtomicLong();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Decision decidePutRequest(Data data) {
        Decision decision = new DecisionImpl(Sequence.valueOf(latestInUseSequence.incrementAndGet()), 
                new DateTime(), GraphRequestType.PUT, data.getId(), data);
        LOGGER.info(String.format("Write decision: Assigned dataId=%s sequence=%d", data.getId(), decision.getSequence().number()));
        bookKeeper.record(decision);
        return decision;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Decision decideDeleteRequest(Data data) {
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
    public List<Decision> readRange(Sequence start, Sequence end) {
        return bookKeeper.readRange(start, end);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Sequence latestDecision() {
        Sequence seq = Sequence.valueOf(latestInUseSequence.get());
        LOGGER.info("Latest in-use decision sequence is: " + seq.number());
        return seq;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void reverse(Decision decision) {
        LOGGER.info(String.format("Wiping out decision with sequence=%d", decision.getSequence().number()));
        bookKeeper.wipe(decision);
    }
    
    @Override
    public synchronized void reverseAll() {
        LOGGER.info("Wiping out ALL decisions.");
        bookKeeper.wipeAll();
        Sequence seq = bookKeeper.lastTransactionId();
        LOGGER.info("Latest in-use decision sequence is: " + seq.number());
        latestInUseSequence.set(seq.number());
    }

}
