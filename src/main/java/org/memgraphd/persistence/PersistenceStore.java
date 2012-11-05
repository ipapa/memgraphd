package org.memgraphd.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

public abstract class PersistenceStore {
    protected final Logger LOGGER = Logger.getLogger(getClass());
    
    private final String dbName;
    
    private final String dbFilePath;
    
    private Connection connection;
    
    protected PersistenceStore(String dbName, String dbFilePath) {
        this.dbName = dbName;
        this.dbFilePath =dbFilePath;
        
        loadJDBCDriver();
        
        connection = createNewConnection();
        
        if(!doesTableExist()) {
            LOGGER.info("Database does not exist, creating it...");
            createDatabase();
        }
    }
    
    protected abstract String createDatabaseSQL();
    
    protected void loadJDBCDriver() {
        try {
            Class.forName(getJDBCDriver());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load jdbc drivers", e);
        }
    }
    
    protected void createDatabase() {
        try {
            Statement stmt = openConnection().createStatement();
            stmt.executeUpdate(createDatabaseSQL());
        } catch(SQLException e) {
            LOGGER.error("Failed to create database.", e);
            throw new RuntimeException(e);
        }
    }
    
    protected boolean doesTableExist() {
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
    
    protected String getJDBCDriver() {
        return "org.hsqldb.jdbc.JDBCDriver";
    }
    
    protected synchronized Connection openConnection() {
        try {
            if(connection == null || connection.isClosed()) {
                LOGGER.error("Databace connection has been closed, opening new one.");
                connection = createNewConnection();
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to check state of data conenction, opening new one.");
            connection = createNewConnection();
        }
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
    
    protected final String getDatabaseName() {
        return dbName;
    }
    
    protected final String getDatabaseFilePath() {
        return dbFilePath;
    }
}
