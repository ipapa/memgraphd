package org.memgraphd.bookkeeper;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

import org.memgraphd.decision.Decision;


/**
 * An implementation of {@link BookKeeper} that uses <a href="http://hsqldb.org/">HSQL</a> as a persistent store.
 * The database will be run in-process mode, embedded with the application and the tables used will be "Cached"
 * meaning the data indexes only will be stored in-memory and the data itself will be stored on disk.
 * 
 * @author Ilirjan Papa
 * @since August 21, 2012
 *
 */
public class HSQLBookKeeper extends AbstractBookKeeper {
    
    /**
     * Constructs a new instance of {@link HSQLBookKeeper}.
     * @param persistenceStore {@link PersistenceStore}
     * @param batchSize size of read/write batch transactions as long.
     * @param writeFrequency how often to write to disk in milliseconds as long.
     */
    public HSQLBookKeeper(PersistenceStore persistenceStore, long batchSize, long writeFrequency) {
        super(persistenceStore, batchSize, writeFrequency);
    }
  
    public void run() {
        
        if(isFlushToDiskTime()) {
            LOGGER.info("Starting the flush to disk proccess");
            getLastFlushedToDisk().set(System.currentTimeMillis());
            Set<Decision> oldBuffer = swapBuffer();
            for(Set<Decision> setOfDescions : splitDecisionsIntoBatches(oldBuffer)) {
                getExecutor().execute(
                        new BookKeeperWriter("BookKeeperWriter-Thread" + getCounter().incrementAndGet(),
                                getPersistenceStore(), setOfDescions));
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void wipe(Decision decision) {
        LOGGER.info(String.format("Wiping out decision with sequence=%d", decision.getSequence().number()));
        Statement stmt = null;
        try {
            stmt = getPersistenceStore().openConnection().createStatement();
            stmt.executeUpdate(
                    String.format("DELETE FROM %s WHERE SEQUENCE_ID=%d;", 
                            getPersistenceStore().getDatabaseName(), decision.getSequence().number()));
            stmt.executeUpdate("COMMIT;");
        } catch (SQLException e) {
            String msg = String.format("Failed to delete decision with sequence=", 
                                            decision.getSequence().number());
            LOGGER.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void wipeAll() {
        LOGGER.info("Wiping out *ALL* transactions from the book");
        try {
            Statement stmt = getPersistenceStore().openConnection().createStatement();
            stmt.executeUpdate(
                    String.format("DELETE FROM %s;", getPersistenceStore().getDatabaseName()));
            stmt.executeUpdate("COMMIT;");
        } catch (SQLException e) {
            String msg = String.format("Failed to delete all decisions.");
            LOGGER.error(msg, e);
            throw new RuntimeException(msg, e);
        }
        
    }
    
}
