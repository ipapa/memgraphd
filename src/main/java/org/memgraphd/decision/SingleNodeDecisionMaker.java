package org.memgraphd.decision;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.memgraphd.GraphLifecycleHandler;
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
public class SingleNodeDecisionMaker implements DecisionMaker, GraphLifecycleHandler {
    private final Logger LOGGER = Logger.getLogger(getClass());
    
    private final BookKeeper bookKeeper;
    private final AtomicLong latestInUseSequence;
    
    public SingleNodeDecisionMaker(BookKeeper bookKeeper) {
        this.bookKeeper = bookKeeper;
        this.latestInUseSequence = new AtomicLong();
        LOGGER.info("Max sequence number in user is: " + latestInUseSequence);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Decision decideWriteRequest(Data data) {
        Decision decision = new DecisionImpl(Sequence.valueOf(latestInUseSequence.incrementAndGet()), 
                new DateTime(), GraphRequestType.PUT, data.getId(), data);
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
        return Sequence.valueOf(latestInUseSequence.get());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void reverse(Decision decision) {
        bookKeeper.wipe(decision);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onStartup() {
        latestInUseSequence.set(bookKeeper.lastTransactionId().number());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onShutdown() {
        // do nothing
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onClearAll() {
        latestInUseSequence.set(bookKeeper.lastTransactionId().number());
    }

}
