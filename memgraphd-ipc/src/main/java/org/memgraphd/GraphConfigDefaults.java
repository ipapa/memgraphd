package org.memgraphd;

import java.sql.SQLException;

import org.memgraphd.bookkeeper.BookKeeper;
import org.memgraphd.bookkeeper.HSQLBookKeeper;
import org.memgraphd.bookkeeper.HSQLPersistenceStore;
import org.memgraphd.bookkeeper.PersistenceStore;
import org.memgraphd.data.library.DefaultLibrary;
import org.memgraphd.data.library.Librarian;
import org.memgraphd.data.library.LibrarySection;
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
    private final LibrarySection[] sections;
    private final MemoryBlockResolver memoryBlockResolver;
    private final String bookKeeperDBName;
    private final String bookKeeperDBPath;
    private final long bookeKeeperBatchSize;
    private final long bookeKeeperWriteFrequency;
    private final DecisionMaker decisionMaker;
    private final BookKeeper bookKeeper;
    private final PersistenceStore persistenceStore;
    private final Librarian librarian;
    
    /**
     * Default constructor that will use predefined default settings to instantiate a new instance.
     * @throws SQLException 
     * @see GraphConfig
     */
    public GraphConfigDefaults() throws SQLException {
        this(DEFAULT_NAME, DEFAULT_CAPACITY, DEFAULT_DB_NAME, DEFAULT_DB_PATH, DEFAULT_BATCH_SIZE,
                DEFAULT_WRITE_FREQUENCY, DEFAULT_LIBRARY_SECTIONS);
    }
    
    /**
     * Constructor that will use predefined default settings to instantiate a new instance with the
     * only exception of the name.
     * @param name the name of the instance as {@link String}
     * @throws SQLException 
     * @see GraphConfig
     */
    public GraphConfigDefaults(String name) throws SQLException {
        this(name, DEFAULT_CAPACITY, DEFAULT_DB_NAME, DEFAULT_DB_PATH, DEFAULT_BATCH_SIZE, 
                DEFAULT_WRITE_FREQUENCY, DEFAULT_LIBRARY_SECTIONS);
    }
    
    /**
     * Constructor that will use predefined default settings to instantiate a new instance with the
     * exception of the name and capacity.
     * @param name the name of the instance as {@link String}
     * @param capacity the capacity objects to store in memory as integer.
     * @throws SQLException 
     * @see GraphConfig
     */
    public GraphConfigDefaults(String name, int capacity) throws SQLException {
        this(name, capacity, DEFAULT_DB_NAME, DEFAULT_DB_PATH, DEFAULT_BATCH_SIZE, 
                DEFAULT_WRITE_FREQUENCY, DEFAULT_LIBRARY_SECTIONS);
    }
    
    /**
     * Constructor that will use predefined default settings to instantiate a new instance with the
     * exception of the name, capacity, dbName, dbPath.
     * @param name the name of the instance as {@link String}
     * @param capacity the capacity objects to store in memory as integer.
     * @param dbName database name to use to store the decisions.
     * @param dbPath the path where to store the database data.
     * @throws SQLException 
     * @see GraphConfig
     */
    public GraphConfigDefaults(String name, int capacity, String dbName, String dbPath) throws SQLException {
        this(name, capacity, dbName, dbPath, DEFAULT_BATCH_SIZE, DEFAULT_WRITE_FREQUENCY,
                DEFAULT_LIBRARY_SECTIONS);
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
     * @param sections library sections as array of {@link LibrarySection}.
     * @throws SQLException 
     * @see GraphConfig
     */
    public GraphConfigDefaults(String name, int capacity, String dbName, String dbPath,
                            long batchSize, long writeFrequency, LibrarySection[] sections) throws SQLException {
        this.name = name;
        this.bookKeeperDBName = dbName;
        this.bookKeeperDBPath = dbPath;
        this.bookeKeeperBatchSize = batchSize;
        this.bookeKeeperWriteFrequency = writeFrequency;
        this.capacity = capacity;
        this.sections = sections;
        this.memoryBlockResolver = new DefaultMemoryBlockResolver(getCapacity());
        this.persistenceStore = new HSQLPersistenceStore(dbName, dbPath);
        this.bookKeeper = new HSQLBookKeeper(getPersistenceStore(),
                                        getBookKeeperOperationBatchSize(), getBookKeeperWriteFrequency());
        this.decisionMaker = new SingleDecisionMaker(getBookKeeper(), batchSize);
        this.librarian = new DefaultLibrary(getLibrarySections());
        
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

    /**
     * {@inheritDoc}
     */
    @Override
    public final PersistenceStore getPersistenceStore() {
        return persistenceStore;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final Librarian getLibrarian() {
        return librarian;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final LibrarySection[] getLibrarySections() {
        return sections;
    }

}
