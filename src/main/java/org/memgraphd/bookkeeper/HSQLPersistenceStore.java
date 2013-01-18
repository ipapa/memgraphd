package org.memgraphd.bookkeeper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

public class HSQLPersistenceStore implements PersistenceStore {
    protected final Logger LOGGER = Logger.getLogger(getClass());
    
    private final String dbName;
    
    private final String dbFilePath;
    
    private final String connectionString;
    
    private static final String JDBC_DRIVER_URL = "org.hsqldb.jdbc.JDBCDriver";
    
    /**
     * 
     * @param dbName Database name as {@link String}
     * @param dbFilePath Database directory file system path as {@link String}
     * @throws SQLException
     */
    public HSQLPersistenceStore(String dbName, String dbFilePath) throws SQLException {
        this.dbName = dbName;
        this.dbFilePath =dbFilePath;
        this.connectionString = String.format("jdbc:hsqldb:file:%s", getDatabaseFilePath());
        
        loadJDBCDriver();
        
        if(!doesTableExist()) {
            LOGGER.info("Database does not exist, creating it...");
            createDatabase();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void loadJDBCDriver() {
        try {
            Class.forName(getJDBCDriver());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load jdbc drivers", e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void createDatabase() throws SQLException {
        Statement stmt = openConnection().createStatement();
        stmt.executeUpdate(createDatabaseSQL());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean doesTableExist() {
        boolean response = true;
        try {
            openConnection().createStatement().executeQuery(
                   String.format(
                           "SELECT COUNT(*) FROM %s WHERE SEQUENCE_ID < 100", getDatabaseName()));

        } catch (SQLException e) {
            response = false;
        }
        return response;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getConnectionString() {
        return connectionString;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getJDBCDriver() {
        return JDBC_DRIVER_URL;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized Connection openConnection() throws SQLException {
        LOGGER.info("Creating a new database connection");
        return DriverManager.getConnection(getConnectionString(), "SA", "");
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void closeConnection() throws SQLException {
        LOGGER.info("Flushing data to disc and compacting database");
        openConnection().createStatement().execute("SHUTDOWN COMPACT;");
        LOGGER.info("Finished compacting the database.");
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final String getDatabaseName() {
        return dbName;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final String getDatabaseFilePath() {
        return dbFilePath;
    }
    
    private String createDatabaseSQL() {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE CACHED TABLE ").append(getDatabaseName());
        builder.append("(SEQUENCE_ID INTEGER NOT NULL PRIMARY KEY,");
        builder.append("DECISION_TIME TIMESTAMP NOT NULL,");
        builder.append("REQUEST_TYPE VARCHAR(10) NOT NULL,");
        builder.append("DATA_ID VARCHAR(100) NOT NULL,");
        builder.append("DATA OBJECT NOT NULL);");
        return builder.toString();
    }
}
