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

import org.memgraphd.decision.Decision;
import org.memgraphd.decision.Sequence;
import org.memgraphd.persistence.PersistenceStore;


public abstract class AbstractBookKeeper extends PersistenceStore implements BookKeeper, Runnable {
    
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
    
    public AbstractBookKeeper(String dbName, String dbFilePath, long batchSize, long writeFrequency) {
        
        super(dbName, dbFilePath);
        
        this.batchSize = batchSize;
        
        this.writeFrequencyMillis = writeFrequency;
        
        this.buffer = new HashSet<Decision>();
    
        reader = new BookKeeperReader(null, dbName, openConnection());
        
    }
    
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
    
    @Override
    public void record(Decision decision) {
        authorize();
        addToBuffer(decision);
    }

    @Override
    public synchronized void closeBook() {
        if(!bookClosed.get()) {
            bookClosed.set(true);
            
            scheduler.shutdown();
            executor.shutdown();
            closeConnection();
        }
    }

    @Override
    public boolean isBookOpen() {
        return !bookClosed.get();
    }

    @Override
    public Sequence lastTransactionId() {
        
        authorize();
        
        try {
            ResultSet rs = openConnection().createStatement().executeQuery(
                    String.format("SELECT MAX(SEQUENCE_ID) FROM %s;", getDatabaseName()));
            while(rs.next()) {
                return Sequence.valueOf(rs.getLong(1));
            }
            return Sequence.valueOf(0);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get last transaction id.", e);
        }
    }
    
    @Override
    public List<Decision> readRange(Sequence start, Sequence end) {
        authorize();
        LOGGER.info(String.format("Reading transcation range from the book start=%d and end=%d.", start.number(), end.number()));
        try {
            
            return reader.readRange(start, end);
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private void authorize() {
        if(bookClosed.get()) {
            throw new RuntimeException("Book is currently closed");
        }
    }
    
    protected boolean isFlushToDiskTime() {
        return  buffer.size() >= batchSize ||
                (System.currentTimeMillis() - lastTimeFlushedToDisk.get()) > writeFrequencyMillis; 
    }
    
    protected Set<Decision> swapBuffer() {
        Set<Decision> oldBuffer = buffer;
        
        resetBuffer();
        
        return oldBuffer;
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
    
    protected final AtomicInteger getCounter() {
        return counter;
    }
    
    protected final ExecutorService getExecutor() {
        return executor;
    }
    
    protected final AtomicLong getLastFlushedToDisk() {
        return lastTimeFlushedToDisk;
    }
   
}
