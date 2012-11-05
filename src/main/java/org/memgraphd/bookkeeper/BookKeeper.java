package org.memgraphd.bookkeeper;

import java.util.List;

import org.memgraphd.decision.Decision;
import org.memgraphd.decision.Sequence;


/**
 * {@link BookKeeper} is in charge of writing and reading decisions from the book. It has exclusive
 * access to the book.
 * 
 * @author Ilirjan Papa
 * @since August 21, 2012
 *
 */
public interface BookKeeper {
    
    /**
     * You need to open the book before you write to it.
     */
    void openBook();
    
    /**
     * Record decision in the book.
     * @param decision {@link Decision}
     */
    void record(Decision decision);
    
    /**
     * It will wipe off the book a pre-recorded decision.
     * @param decision {@link Decision}
     */
    void wipe(Decision decision);
    
    /**
     * Read a range of decisions from the book starting at start and ending at end sequence.
     * @param start {@link Sequence}
     * @param end {@link Sequence}
     * @return {@link List} of {@link Decision}
     */
    List<Decision> readRange(Sequence start, Sequence end);
    
    /**
     * When you are done writing/reading from the book, you need to close it.
     */
    void closeBook();
    
    /**
     * Checks to see if the book is open.
     * @return true if it is open, false otherwise.
     */
    boolean isBookOpen();
    
    /**
     * Returns the last good known decision sequence that has been made.
     * Since sequences are always ordered, this is the theoretical max number of decisions in the book.
     * @return {@link Sequence}
     */
    Sequence lastTransactionId();
}
