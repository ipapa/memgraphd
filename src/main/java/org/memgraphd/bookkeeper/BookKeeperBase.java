package org.memgraphd.bookkeeper;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
/**
 * The base class for a BookKeeper, abstracts out the common shared functionality of a book-keeper
 * in case we choose to implement it using different persistent stores.
 * 
 * @author Ilirjan Papa
 * @since August 21, 2012
 *
 */
public class BookKeeperBase {
    protected final Logger LOGGER = Logger.getLogger(getClass());
    
    private final String threadName;
    private final String dbName;
    private final Connection connection;
    
    private final AtomicBoolean completed = new AtomicBoolean(false);
    private final AtomicBoolean errorOccurred = new AtomicBoolean(false);
    
    public BookKeeperBase(String threadName, String dbName, Connection connection) {
        this.threadName = threadName;
        this.dbName = dbName;
        this.connection = connection;
    }
    
    protected final String getThreadName() {
        return threadName;
    }
    
    protected final String getDbName() {
        return dbName;
    }
    
    protected final Connection getConnection() {
        return connection;
    }
    
    protected final void completed() throws SQLException {
        completed.set(true);
        getConnection().createStatement().executeUpdate("COMMIT;");
    }
    
    protected final void error() {
        errorOccurred.set(true);
    }
    
    public final boolean hasRun() {
        return isSuccess() || isFailure();
    }
    
    public final boolean isSuccess() {
        return completed.get();
    }
    
    public final boolean isFailure() {
        return errorOccurred.get();
    }
}
