package org.memgraphd;

import org.memgraphd.bookkeeper.BookKeeper;
import org.memgraphd.bookkeeper.HSQLBookKeeper;
import org.memgraphd.decision.DecisionMaker;
import org.memgraphd.decision.SingleDecisionMaker;
import org.memgraphd.memory.DefaultMemoryBlockResolver;
import org.memgraphd.memory.MemoryBlockResolver;

/**
 * This is the default {@link GraphConfig} instance that the {@link Graph} will use
 * to instantiate a new instance, if the caller does not provide its own {@link GraphConfig} instance.
 * 
 * @author Ilirjan Papa
 * @since November 16, 2012
 *
 */
public class GraphConfigDefaults implements GraphConfig {
    
    private final String name;
    private final int capacity;
    private final MemoryBlockResolver memoryBlockResolver;
    private final DecisionMaker decisionMaker;
    private final BookKeeper bookKeeper;
    private final String bookKeeperDBName;
    private final String bookKeeperDBPath;
    private final long bookeKeeperBatchSize;
    private final long bookeKeeperWriteFrequency;
    
    /**
     * Default constructor that will use predefined default settings to instantiate a new instance.
     * @see GraphConfig
     */
    public GraphConfigDefaults() {
        this(DEFAULT_NAME, DEFAULT_CAPACITY, DEFAULT_DB_NAME, DEFAULT_DB_PATH, DEFAULT_BATCH_SIZE, DEFAULT_WRITE_FREQUENCY);
    }
    
    /**
     * Constructor that will use predefined default settings to instantiate a new instance with the
     * only exception of the name.
     * @param name the name of the instance as {@link String}
     * @see GraphConfig
     */
    public GraphConfigDefaults(String name) {
        this(name, DEFAULT_CAPACITY, DEFAULT_DB_NAME, DEFAULT_DB_PATH, DEFAULT_BATCH_SIZE, DEFAULT_WRITE_FREQUENCY);
    }
    
    /**
     * Constructor that will use predefined default settings to instantiate a new instance with the
     * exception of the name and capacity.
     * @param name the name of the instance as {@link String}
     * @param capacity the capacity objects to store in memory as integer.
     * @see GraphConfig
     */
    public GraphConfigDefaults(String name, int capacity) {
        this(name, capacity, DEFAULT_DB_NAME, DEFAULT_DB_PATH, DEFAULT_BATCH_SIZE, DEFAULT_WRITE_FREQUENCY);
    }
    
    /**
     * Constructor that will use predefined default settings to instantiate a new instance with the
     * exception of the name, capacity, dbName, dbPath.
     * @param name the name of the instance as {@link String}
     * @param capacity the capacity objects to store in memory as integer.
     * @param dbName database name to use to store the decisions.
     * @param dbPath the path where to store the database data.
     * @see GraphConfig
     */
    public GraphConfigDefaults(String name, int capacity, String dbName, String dbPath) {
        this(name, capacity, dbName, dbPath, DEFAULT_BATCH_SIZE, DEFAULT_WRITE_FREQUENCY);
    }
    
    /**
     * Constructor that will use predefined default settings to instantiate a new instance with the
     * exception of the name, capacity and dbName.
     * @param name the name of the instance as {@link String}
     * @param capacity the capacity objects to store in memory as integer.
     * @param dbName database name to use to store the decisions.
     * @param dbPath the path where to store the database data.
     * @param batchSize how many decisions to read or write in a batch transaction
     * @param writeFrequency long frequency in milliseconds to persist to disk decisions already made.
     * @see GraphConfig
     */
    public GraphConfigDefaults(String name, int capacity, String dbName, String dbPath,
                            long batchSize, long writeFrequency) {
        this.name = name;
        this.bookKeeperDBName = dbName;
        this.bookKeeperDBPath = dbPath;
        this.bookeKeeperBatchSize = batchSize;
        this.bookeKeeperWriteFrequency = writeFrequency;
        this.capacity = capacity;
        this.memoryBlockResolver = new DefaultMemoryBlockResolver(getCapacity());
        this.bookKeeper = new HSQLBookKeeper(getBookKeeperDatabaseName(), getBookKeeperDatabasePath(),
                                        getBookKeeperOperationBatchSize(), getBookKeeperWriteFrequency());
        this.decisionMaker = new SingleDecisionMaker(getBookKeeper(), batchSize);
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final String getName() {
        return name;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final int getCapacity() {
        return capacity;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final String getBookKeeperDatabaseName() {
        return bookKeeperDBName;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final String getBookKeeperDatabasePath() {
        return bookKeeperDBPath;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final MemoryBlockResolver getMemoryBlockResolver() {
        return memoryBlockResolver;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final DecisionMaker getDecisionMaker() {
        return decisionMaker;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final BookKeeper getBookKeeper() {
        return bookKeeper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final long getBookKeeperOperationBatchSize() {
        return bookeKeeperBatchSize;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final long getBookKeeperWriteFrequency() {
        return bookeKeeperWriteFrequency;
    }

}
