package org.memgraphd;

import org.memgraphd.bookkeeper.BookKeeper;
import org.memgraphd.bookkeeper.PersistenceStore;
import org.memgraphd.data.Data;
import org.memgraphd.decision.Decision;
import org.memgraphd.decision.DecisionMaker;
import org.memgraphd.memory.MemoryBlock;
import org.memgraphd.memory.MemoryBlockResolver;

/**
 * Configuration object to be used during {@link Graph}'s initialization phase.
 * @author Ilirjan Papa
 * @since November 16, 2012
 *
 */
public interface GraphConfig {
    /**
     * Default name to be used by the {@link Graph} if one is not provided.
     */
    static final String DEFAULT_NAME = "MyGraph";
    
    /**
     * Default capacity to be used by the {@link Graph} if one is not provided.
     */
    static final int DEFAULT_CAPACITY = 100;
    
    /**
     * Default database name to be used by {@link BookKeeper} if one is not provided.
     */
    static final String DEFAULT_DB_NAME = "PUBLIC.BOOK";
    
    /**
     * Default database path to be used by {@link BookKeeper} if one is not provided.
     */
    static final String DEFAULT_DB_PATH = "/tmp/book/";
    
    /**
     * Default size of a batch operation by {@link BookKeeper} to read/write {@link Decision}(s).
     */
    static final long DEFAULT_BATCH_SIZE = 1000L;
    
    /**
     * Default time interval to be used by {@link BookKeeper} to write decisions to disk.
     */
    static final long DEFAULT_WRITE_FREQUENCY = 2000L;
    
    /**
     * Returns the {@link Graph}'s name to use. 
     * @return {@link String}
     */
    String getName();
    
    /**
     * The capacity of the Graph, the maximum number of {@link Data} instances we can store.
     * @return integer
     */
    int getCapacity();
    
    /**
     * Returns {@link BookKeeper}'s database name to use when creating the database.
     * @return {@link String}
     */
    String getBookKeeperDatabaseName();
    
    /**
     * Returns the file-system path where to store the {@link BookKeeper}'s transaction long.
     * @return {@link String}
     */
    String getBookKeeperDatabasePath();
    
    /**
     * Returns the number of read/write operations {@link BookKeeper} in a batch transaction.
     * @return long
     */
    long getBookKeeperOperationBatchSize();
    
    /**
     * Returns the time interval in milliseconds to write buffered decisions in the book.
     * This setting is related to {@link #getBookKeeperOperationBatchSize()}.
     * @return long
     */
    long getBookKeeperWriteFrequency();
    
    /**
     * In charge of determining in which memory {@link MemoryBlock} to store {@link Data} that
     * gets written in the {@link Graph}.
     * @return {@link MemoryBlockResolver}
     */
    MemoryBlockResolver getMemoryBlockResolver();
    
    /**
     * Returns the {@link DecisionMaker} instance that the {@link Graph} will use to order PUT/DELETE request.
     * @return {@link DecisionMaker}
     */
    DecisionMaker getDecisionMaker();
    
    /**
     * Returns the {@link BookKeeper} instances that the {@link DecisionMaker} will use to persist decisions.
     * @return {@link BookKeeper}
     */
    BookKeeper getBookKeeper();
    
    /**
     * Returns the persistence store implementation to use.
     * @return {@link PersistenceStore}
     */
    PersistenceStore getPersistenceStore();
    
}
