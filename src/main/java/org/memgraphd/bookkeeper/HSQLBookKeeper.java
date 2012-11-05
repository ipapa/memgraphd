package org.memgraphd.bookkeeper;

import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;
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
    private static final Logger LOGGER = Logger.getLogger(HSQLBookKeeper.class);
    
    /**
     * Default name of the database table to store book data.
     */
    public static final String DEFAULT_TABLE_NAME = "PUBLIC.BOOK";
    
    /**
     * Default database file path where all database files will be created.
     */
    public static final String DEFAULT_DB_FILE_PATH = "/tmp/book/";

    public HSQLBookKeeper() {
        super(DEFAULT_TABLE_NAME, DEFAULT_DB_FILE_PATH);
    }
  
    public void run() {
        
        if(isFlushToDiskTime()) {
            LOGGER.info("Starting the flush to disk proccess");
            getLastFlushedToDisk().set(System.currentTimeMillis());
            Set<Decision> oldBuffer = swapBuffer();
            for(Set<Decision> setOfDescions : splitDecisionsIntoBatches(oldBuffer)) {
                getExecutor().execute(
                        new BookKeeperWriter("BookKeeperWriter-Thread" + getCounter().incrementAndGet(),
                                getDatabaseName(), openConnection(), setOfDescions));
            }
        }
    }

    @Override
    protected String createDatabaseSQL() {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE CACHED TABLE ").append(getDatabaseName());
        builder.append("(SEQUENCE_ID INTEGER NOT NULL PRIMARY KEY,");
        builder.append("DECISION_TIME TIMESTAMP NOT NULL,");
        builder.append("REQUEST_TYPE VARCHAR(10) NOT NULL,");
        builder.append("DATA_ID VARCHAR(100) NOT NULL,");
        builder.append("DATA OBJECT NOT NULL);");
        return builder.toString();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void wipe(Decision decision) {
        try {
            openConnection().createStatement().executeUpdate(
                    String.format("DELETE FROM %s WHERE SEQUENCE_ID=%d", 
                            getDatabaseName(), decision.getSequence().number()));
        } catch (SQLException e) {
            String msg = String.format("Failed to delete decision with sequence=", 
                                            decision.getSequence().number());
            LOGGER.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }
    
}
