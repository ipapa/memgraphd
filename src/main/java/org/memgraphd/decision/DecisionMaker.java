package org.memgraphd.decision;

import java.util.List;

import org.memgraphd.bookkeeper.BookKeeper;
import org.memgraphd.request.GraphRequest;

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
     * Given an incoming request, please make a decision about it.
     * @param request {@link GraphRequest}
     * @return {@link Decision}
     */
    Decision decide(GraphRequest request);
    
    /**
     * Return the last-good-known decision sequence made by this decision maker.
     * @return {@link Sequence}
     */
    Sequence latestDecision();
    
    /**
     * Returns list of decisions in between this range of sequences.
     * @param startSeq start {@link Sequence}
     * @param endSequence end {@link Sequence}
     * @return
     */
    List<Decision> readRange(Sequence startSeq, Sequence endSequence);
    
    /**
     * <b>WARNING:</b> Calling this method will delete all decision ever made.
     */
    void clearAll();
}
