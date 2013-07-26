package org.memgraphd.bookkeeper;

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
public abstract class BookKeeperBase {
    protected final Logger LOGGER = Logger.getLogger(getClass());
    
    private final String threadName;
    private final PersistenceStore persistenceStore;
    
    private final AtomicBoolean completed = new AtomicBoolean(false);
    private final AtomicBoolean errorOccurred = new AtomicBoolean(false);
    
    /**
     * Constructs an instance of {@link BookKeeperBase} that stores the basic data.
     * @param threadName thread name as {@link String}
     * @param persistenceStore {@link PersistenceStore}
     */
    protected BookKeeperBase(String threadName, PersistenceStore persistenceStore) {
        this.threadName = threadName;
        this.persistenceStore = persistenceStore;
    }
    
    /**
     * Returns the name of this thread.
     * @return {@link String}
     */
    protected final String getThreadName() {
        return threadName;
    }
    
    /**
     * Invoke this method when operation completed successfully.
     * @throws SQLException
     */
    protected final void completed() throws SQLException {
        completed.set(true);
        persistenceStore.openConnection().createStatement().executeUpdate("COMMIT;");
    }
    
    /**
     * Invoke this method when an error has occurred when running an operation.
     */
    protected final void error() {
        errorOccurred.set(true);
    }
    
    /**
     * Returns true if this thread has already run and is done, otherwise false.
     * @return true if thread is finished: completed successfully or failed, false it is still running.
     */
    public final boolean hasRun() {
        return isSuccess() || isFailure();
    }
    
    /**
     * Returns true if the operation has completed successfully, otherwise false.
     * @return boolean
     */
    public final boolean isSuccess() {
        return completed.get();
    }
    
    /**
     * Returns true if the operation has failed, otherwise true.
     * @return boolean
     */
    public final boolean isFailure() {
        return errorOccurred.get();
    }
    
    /**
     * Returns the {@link PersistenceStore} implementation used to persist data.
     * @return {@link PersistenceStore}
     */
    public final PersistenceStore getPersistenceStore() {
        return persistenceStore;
    }
}
