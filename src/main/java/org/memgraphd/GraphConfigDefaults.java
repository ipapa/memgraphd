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
    
    public GraphConfigDefaults() {
        this(DEFAULT_NAME, DEFAULT_CAPACITY, DEFAULT_DB_NAME, DEFAULT_DB_PATH);
    }
    
    public GraphConfigDefaults(String name) {
        this(name, DEFAULT_CAPACITY, DEFAULT_DB_NAME, DEFAULT_DB_PATH);
    }
    
    public GraphConfigDefaults(String name, int capacity) {
        this(name, capacity, DEFAULT_DB_NAME, DEFAULT_DB_PATH);
    }
    
    public GraphConfigDefaults(String name, int capacity, String dbName) {
        this(name, capacity, dbName, DEFAULT_DB_PATH);
    }
    
    public GraphConfigDefaults(String name, int capacity, String dbName, String dbPath) {
        this.name = name;
        this.bookKeeperDBName = dbName;
        this.bookKeeperDBPath = dbPath;
        this.capacity = capacity;
        this.memoryBlockResolver = new DefaultMemoryBlockResolver(getCapacity());
        this.bookKeeper = new HSQLBookKeeper(getBookKeeperDatabaseName(), getBookKeeperDatabasePath());
        this.decisionMaker = new SingleDecisionMaker(getBookKeeper());
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

}
