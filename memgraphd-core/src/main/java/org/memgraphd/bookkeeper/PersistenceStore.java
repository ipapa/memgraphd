package org.memgraphd.bookkeeper;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * This is the interface we will use to interact with the persistence store 
 * which is expected to be a database. Implement this interface if you want 
 * to use your own persistence store database.
 * 
 * @author Ilirjan Papa
 * @since January 17, 2013
 *
 */
public interface PersistenceStore {
    /**
     * The path to the directory where database data will be stored.
     * @return String
     */
    String getDatabaseFilePath();
    
    /**
     * The name of the database to use to store our data.
     * @return String
     */
    String getDatabaseName();
    
    /**
     * Returns the JDBC driver URL as String.
     * @return String
     */
    String getJDBCDriver();
    
    /**
     * Invoking this method will force loading the JDBC driver classes on the JVM.
     */
    void loadJDBCDriver();
    
    /**
     * Returns the constructed connection string to use to connect to the database.
     * @return String
     */
    String getConnectionString();
    
    /**
     * Opens a new connection to the database.
     * @return {@link Connection}
     * @throws SQLException
     */
    Connection openConnection() throws SQLException;
    
    /**
     * Closed the connection to the database and compacts the data on shutdown.
     * @throws SQLException
     */
    void closeConnection() throws SQLException;
    
    /**
     * Checks to makes sure the database exists.
     * @return true if database already exists, false otherwise.
     */
    boolean doesTableExist();
    
    /**
     * Invoking this method will create the database.
     * @throws SQLException
     */
    void createDatabase() throws SQLException;
}
