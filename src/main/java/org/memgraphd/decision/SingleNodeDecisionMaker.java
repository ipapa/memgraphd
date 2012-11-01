package org.memgraphd.decision;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.memgraphd.GraphLifecycleHandler;
import org.memgraphd.bookkeeper.BookKeeper;
import org.memgraphd.request.GraphRequest;

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
    public Decision decide(GraphRequest request) {
        Decision decision = new DecisionImpl(request, Sequence.valueOf(latestInUseSequence.incrementAndGet()), new DateTime());
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
    public void clearAll() {
        LOGGER.info("Deleting all decisions I have made.");
        bookKeeper.deleteAll();
        latestInUseSequence.set(0L);
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

}
