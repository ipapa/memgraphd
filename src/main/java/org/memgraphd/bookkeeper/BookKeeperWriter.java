package org.memgraphd.bookkeeper;

import java.sql.Connection;
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
    
    public BookKeeperWriter(String threadName, String dbName, Connection connection, Set<Decision> decisions) {
        super(threadName, dbName, connection);
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
        PreparedStatement insert = createStatement();
        try {
            for(Decision d : decisions) {
                insert.setLong(1, d.getSequence().number());
                insert.setTimestamp(2, new Timestamp(d.getTime().getMillis()));
                insert.setString(3, d.getRequest().getType().name());
                insert.setString(4, d.getRequest().getRequestUri());
                insert.setTimestamp(5, new Timestamp(d.getTime().getMillis()));
                insert.setString(6, d.getRequest().getData().toString());
                
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
    
    private PreparedStatement createStatement() {
        try {
            return getConnection().prepareStatement(String.format("INSERT INTO %s VALUES (?,?,?, ?,?,?);", getDbName()));
        } catch (SQLException e) {
            LOGGER.error("Failed to crated a prepared statement", e);
            throw new RuntimeException(e);
        }
    }
}