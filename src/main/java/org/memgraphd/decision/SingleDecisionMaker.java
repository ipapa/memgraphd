package org.memgraphd.decision;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.memgraphd.GraphLifecycleHandler;
import org.memgraphd.GraphRequestType;
import org.memgraphd.bookkeeper.BookKeeper;
import org.memgraphd.bookkeeper.HSQLBookKeeper;
import org.memgraphd.data.Data;

/**
 * A single node implementation of a {@link DecisionMaker} meant to be used when running on a single JVM.
 * 
 * @author Ilirjan Papa
 * @since July 27, 2012
 *
 */
public class SingleDecisionMaker implements DecisionMaker, GraphLifecycleHandler {
    private final Logger LOGGER = Logger.getLogger(getClass());
    
    private final BookKeeper bookKeeper;
    private final AtomicLong latestInUseSequence;
    
    public SingleDecisionMaker() {
        this.bookKeeper = new HSQLBookKeeper();
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
        LOGGER.info(String.format("Write decision made: Assigned dataId=%s sequence=%d", data.getId(), decision.getSequence().number()));
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
        LOGGER.info(String.format("Delete decision made: Assigned dataId=%s sequence=%d", data.getId(), decision.getSequence().number()));
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
        if(bookKeeper instanceof GraphLifecycleHandler) {
            GraphLifecycleHandler handler = (GraphLifecycleHandler) bookKeeper;
            handler.onStartup();
        }
        latestInUseSequence.set(bookKeeper.lastTransactionId().number());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void onShutdown() {
        if(bookKeeper instanceof GraphLifecycleHandler) {
            GraphLifecycleHandler handler = (GraphLifecycleHandler) bookKeeper;
            handler.onShutdown();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void onClearAll() {
        if(bookKeeper instanceof GraphLifecycleHandler) {
            GraphLifecycleHandler handler = (GraphLifecycleHandler) bookKeeper;
            handler.onClearAll();
        }
        latestInUseSequence.set(bookKeeper.lastTransactionId().number());
    }

}
