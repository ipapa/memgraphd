package org.memgraphd.bookkeeper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.memgraphd.decision.Decision;
import org.memgraphd.decision.Sequence;


public abstract class AbstractBookKeeper implements BookKeeper, Runnable {
    
    protected final Logger LOGGER = Logger.getLogger(getClass());
    
    private final PersistenceStore persistenceStore;
    
    private Set<Decision> buffer;
   
    private final ReentrantLock bufferLock = new ReentrantLock();
    
    private ScheduledExecutorService scheduler;
    
    private ExecutorService executor;
    
    private final BookKeeperReader reader;
    
    private final AtomicLong lastTimeFlushedToDisk = new AtomicLong();
    
    private AtomicInteger counter = new AtomicInteger();
    
    private volatile AtomicBoolean bookClosed = new AtomicBoolean(true);
    
    private final long batchSize;
    
    private final long writeFrequencyMillis;
    
    public AbstractBookKeeper(PersistenceStore persistenceStore, long batchSize, long writeFrequency) {
        
        this.persistenceStore = persistenceStore;
        
        this.batchSize = batchSize;
        
        this.writeFrequencyMillis = writeFrequency;
        
        this.buffer = new HashSet<Decision>();
    
        reader = new BookKeeperReader(null, persistenceStore);
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void openBook() {
        if(bookClosed.get()) {
            bookClosed.set(false);
            
            lastTimeFlushedToDisk.set(System.currentTimeMillis());
            ThreadFactory threadFactory = new ThreadFactory() {
                
                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r);
                }
            };
            scheduler = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors(), threadFactory);
            scheduler.schedule(this, writeFrequencyMillis, TimeUnit.MILLISECONDS);
            
            executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void record(Decision decision) {
        authorize();
        addToBuffer(decision);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void closeBook() {
        if(!bookClosed.get()) {
            bookClosed.set(true);
            
            scheduler.shutdown();
            executor.shutdown();
            try {
                persistenceStore.closeConnection();
            } catch (SQLException e) {
                LOGGER.error("Failed to close connection to persistence store", e);
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBookOpen() {
        return !bookClosed.get();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Sequence lastTransactionId() {
        
        authorize();
        
        try {
            ResultSet rs = persistenceStore.openConnection().createStatement().executeQuery(
                    String.format("SELECT MAX(SEQUENCE_ID) FROM %s;", persistenceStore.getDatabaseName()));
            while(rs.next()) {
                return Sequence.valueOf(rs.getLong(1));
            }
            return Sequence.valueOf(0);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get last transaction id.", e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final List<Decision> readRange(Sequence start, Sequence end) {
        authorize();
        LOGGER.info(String.format("Reading transcation range from the book start=%d and end=%d.", start.number(), end.number()));
        try {
            
            return reader.readRange(start, end);
            
        } catch (SQLException e) {
            throw new RuntimeException("SQL exception occurred...", e);
        }
    }
    
    /**
     * Returns true if it is time to flush buffered decisions to persistence store, otherwise false.
     * @return boolean
     */
    protected boolean isFlushToDiskTime() {
        return  buffer.size() >= batchSize ||
                (System.currentTimeMillis() - lastTimeFlushedToDisk.get()) > writeFrequencyMillis; 
    }
    
    /**
     * Swaps the in-use decision buffer with a new empty buffer, returns the old buffer.
     * @return {@link Set} of {@link Decision}(s).
     */
    protected Set<Decision> swapBuffer() {
        Set<Decision> oldBuffer = buffer;
        
        resetBuffer();
        
        return oldBuffer;
    }
    
    /**
     * Returns a list of batched decisions into sets of a certain batch size.
     * @param decisions {@link Set} of {@link Decision}(s).
     * @return {@link List} of {@link Set} of {@link Decision}
     */
    protected List<Set<Decision>> splitDecisionsIntoBatches(Set<Decision> decisions) {
        List<Set<Decision>> batchSet = new ArrayList<Set<Decision>>();
        Set<Decision> newSet = new HashSet<Decision>();
        for(Decision d : decisions) {
            if(newSet.size() < batchSize) {
                newSet.add(d);
            }
            else {
                batchSet.add(newSet);
                newSet = new HashSet<Decision>();
                newSet.add(d);
            }
        }
        batchSet.add(newSet);

        return batchSet;
    }
    
    /**
     * Returns the counter.
     * @return {@link AtomicInteger}
     */
    protected final AtomicInteger getCounter() {
        return counter;
    }
    
    /**
     * Returns the instance of {@link ExecutorService} to use to run tasks.
     * @return {@link ExecutorService}
     */
    protected ExecutorService getExecutor() {
        return executor;
    }
    
    /**
     * Returns the last time as long that the buffer was flushed to persistence store.
     * @return {@link AtomicLong}
     */
    protected final AtomicLong getLastFlushedToDisk() {
        return lastTimeFlushedToDisk;
    }
    
    /**
     * Returns the implementation of {@link PersistenceStore} that is currently in-use.
     * @return {@link PersistenceStore}
     */
    protected final PersistenceStore getPersistenceStore() {
        return persistenceStore;
    }

    private void authorize() {
        if(bookClosed.get()) {
            throw new RuntimeException("Book is currently closed");
        }
    }

    private void resetBuffer() {
        bufferLock.lock();
        buffer = new HashSet<Decision>();   
        bufferLock.unlock();
    }

    private void addToBuffer(Decision decision) {
        bufferLock.lock();
        buffer.add(decision);
        bufferLock.unlock();
    }
}
