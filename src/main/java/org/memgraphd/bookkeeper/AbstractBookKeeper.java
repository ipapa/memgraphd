package org.memgraphd.bookkeeper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
import org.memgraphd.GraphLifecycleHandler;
import org.memgraphd.decision.Decision;
import org.memgraphd.decision.Sequence;


public abstract class AbstractBookKeeper implements BookKeeper, Runnable, GraphLifecycleHandler {
    
    protected final Logger LOGGER = Logger.getLogger(getClass());
    
    protected static final long WRITE_FREQUENCY = 3000L;

    protected static final int BATCH_SIZE = 10000;
    
    protected final String dbName;
    
    protected final String dbFilePath;
    
    protected volatile AtomicBoolean bookClosed = new AtomicBoolean(true);
    
    protected Set<Decision> buffer;
   
    protected final ReentrantLock bufferLock = new ReentrantLock();
    
    protected ScheduledExecutorService scheduler;
    
    protected ExecutorService executor;
    
    private final Connection connection;
    
    private final BookKeeperReader reader;
    
    protected final AtomicLong lastTimeFlushedToDisk = new AtomicLong();
    
    protected AtomicInteger counter = new AtomicInteger();
    
    public AbstractBookKeeper(String dbName, String dbFilePath) throws SQLException {
        this.dbName = dbName;
        this.dbFilePath =dbFilePath;
        this.buffer = new HashSet<Decision>();
        
        loadJDBCDriver();
        
        connection = createNewConnection();
        
        reader = new BookKeeperReader(null, dbName, connection);
        
        if(!doesTableExist()) {
            LOGGER.info("Database does not exist, creating it...");
            createDatabase();
        }
        
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
            scheduler.schedule(this, WRITE_FREQUENCY, TimeUnit.MILLISECONDS);
            
            executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        }
    }
    
    protected abstract void createDatabase();
    
    protected String getJDBCDriver() {
        return "org.hsqldb.jdbc.JDBCDriver";
    }
    
    protected synchronized Connection openConnection() {
        return connection;
    }
    
    protected Connection createNewConnection() {
        try {
            LOGGER.info("Creating a new database connection");
            String connectionString = String.format("jdbc:hsqldb:file:%s;", dbFilePath);
            
            return DriverManager.getConnection(connectionString, "SA", "");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to hsql db", e);
        }
    }

    protected void closeConnection() {
        try {
            LOGGER.info("Flushing data to disc and compacting database");
            openConnection().createStatement().execute("SHUTDOWN COMPACT;");
            LOGGER.info("Finished compacting the database.");
        } catch (SQLException e) {
            LOGGER.error("Failed to compact and shutdown database connection", e);
        }
    }

    protected boolean doesTableExist() {
        boolean response = true;
        try {
            openConnection().createStatement().executeQuery(
                   String.format(
                           "SELECT COUNT(*) FROM %s WHERE DECISION_SEQUENCE < 100", dbName));

        } catch (SQLException e) {
            response = false;
        }
        return response;
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
            ResultSet rs = openConnection().createStatement().executeQuery(String.format("SELECT MAX(DECISION_SEQUENCE) FROM %s;", dbName));
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
    
    @Override
    public synchronized void deleteAll() {
        authorize();
        LOGGER.info("Deleting all transcations from the book. Size=" + lastTransactionId());
        try {
            PreparedStatement statement = openConnection().prepareStatement(
                    String.format("DELETE FROM %s;", dbName));
          statement.execute();
          lastTimeFlushedToDisk.set(0);
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
        return  buffer.size() >= BATCH_SIZE ||
                (System.currentTimeMillis() - lastTimeFlushedToDisk.get()) > WRITE_FREQUENCY; 
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

    protected void loadJDBCDriver() {
        try {
            Class.forName(getJDBCDriver());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load jdbc drivers", e);
        }
    }
    
    protected List<Set<Decision>> splitDecisionsIntoBatches(Set<Decision> decisions) {
        List<Set<Decision>> batchSet = new ArrayList<Set<Decision>>();
        Set<Decision> newSet = new HashSet<Decision>();
        for(Decision d : decisions) {
            if(newSet.size() < BATCH_SIZE) {
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
    
    @Override
    public void onStartup() {
        openBook();
    }
    
    @Override
    public void onShutdown() {
        closeBook();
    }
}
