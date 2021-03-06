package org.memgraphd.decision;

import java.util.List;

import org.memgraphd.bookkeeper.BookKeeper;
import org.memgraphd.exception.GraphException;
import org.memgraphd.security.GraphRequestContext;

/**
 * The {@link DecisionMaker} is in charge of making decisions about the write/delete events that change
 * the state of our data. Its main responsibility is to order these events in a consistent manner. The second
 * responsibility is to write/write these decisions into the book using the {@link BookKeeper} internally.
 * 
 * @author Ilirjan Papa
 * @since July 27, 2012
 *
 */
public interface DecisionMaker {
    
    /**
     * It makes a decision on the order in which this request should be executed.
     * @param context {@link GraphRequestContext}
     * @return {@link Decision}
     * @throws GraphException
     */
    Decision decide(GraphRequestContext context) throws GraphException;
    
    /**
     * Return the last-good-known decision sequence made by this decision maker.
     * @return {@link Sequence}
     */
    Sequence latestDecision();
    
    /**
     * Returns list of decisions in between this range of sequences.
     * @param startSeq start {@link Sequence}
     * @param endSequence end {@link Sequence}
     * @return {@link List} of {@link Decision}(s).
     */
    List<Decision> readRange(Sequence startSeq, Sequence endSequence);
    
    /**
     * Reverses an enacted decision, which is no longer valid.
     * @param decision {@link Decision}
     */
    void reverse(Decision decision);
    
    /**
     * Reverses <b>ALL</b> enacted decisions. This action cannot be undone.
     */
    void reverseAll();
    
    /**
     * Returns the number of decisions that will read or write at a time as part of a batch operation.
     * @return long
     */
    long getReadWriteBatchSize();

}
