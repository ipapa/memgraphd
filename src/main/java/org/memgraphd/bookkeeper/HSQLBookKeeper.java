package org.memgraphd.bookkeeper;

import java.sql.SQLException;
import java.sql.Statement;
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
    
    public HSQLBookKeeper(String dbName, String dbFilePath) throws SQLException {
        super(dbName, dbFilePath);
    }
    
    @Override
    protected void createDatabase() {
        try {
            Statement stmt = openConnection().createStatement();
            stmt.executeUpdate(String.format("CREATE CACHED TABLE %s(DECISION_SEQUENCE INTEGER NOT NULL PRIMARY KEY, DECISION_TIME TIMESTAMP NOT NULL, REQUEST_TYPE VARCHAR(10) NOT NULL, REQUEST_URI VARCHAR(100) NOT NULL, REQUEST_TIME TIMESTAMP NOT NULL, REQUEST_DATA VARCHAR(10000));", getDatabaseName()));
        } catch(SQLException e) {
            LOGGER.error("Failed to create database.", e);
            throw new RuntimeException(e);
        }
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
    
}
