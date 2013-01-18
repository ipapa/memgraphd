package org.memgraphd.bookkeeper;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;

import org.memgraphd.decision.Decision;

/**
 * A thread in charge of writing batches of decision to the book on behalf of the book-keeper.
 * This thread should be submitted to a {@link ScheduledExecutorService} so it can be triggered on a schedule.
 * 
 * @author Ilirjan Papa
 * @since August 22, 2012
 * @see BookKeeper
 */
class BookKeeperWriter extends BookKeeperBase implements Runnable {

    private final Set<Decision> decisions;
    
    /**
     * Constructs a new instance of {@link BookKeeperWriter}.
     * 
     * @param threadName Thread name as {@link String}.
     * @param persistenceStore {@link PersistenceStore}
     * @param decisions {@link Set} of {@link Decision}(s).
     */
    public BookKeeperWriter(String threadName, PersistenceStore persistenceStore, Set<Decision> decisions) {
        super(threadName, persistenceStore);
        this.decisions = decisions;
    }
    
    @Override
    public void run() {
        if(!decisions.isEmpty()) {
            flushBufferToDisk(decisions);
            decisions.clear();
        }
    }
    
    private void flushBufferToDisk(Set<Decision> decisions) throws RuntimeException {
        long start = System.currentTimeMillis();
        try {
            PreparedStatement insert = getPersistenceStore().openConnection().prepareStatement(
                    String.format("INSERT INTO %s VALUES (?,?,?,?,?);", getPersistenceStore().getDatabaseName()));
            for(Decision d : decisions) {
                insert.setLong(1, d.getSequence().number());
                insert.setTimestamp(2, new Timestamp(d.getTime().getMillis()));
                insert.setString(3, d.getRequestType().name());
                insert.setString(4, d.getDataId());
                insert.setObject(5, d.getData());
                
                LOGGER.debug(insert.toString());
                insert.addBatch();
            }
            
            insert.executeBatch();       
            completed();
       
        } catch (SQLException e) {
            error();
            LOGGER.error(String.format("%s failed to write transactions.", getThreadName()), e);
            throw new RuntimeException("Failed to write to disk statement=", e);
        }
        LOGGER.info(String.format("Thread %s finished writing %d decisions in %d millis. Time: %d", getThreadName(), decisions.size(), (System.currentTimeMillis() - start), System.currentTimeMillis()));
    }

}