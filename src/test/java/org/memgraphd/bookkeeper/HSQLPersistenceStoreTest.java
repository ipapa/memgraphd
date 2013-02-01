package org.memgraphd.bookkeeper;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import static org.mockito.Matchers.anyString;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HSQLPersistenceStoreTest {
    
    private HSQLPersistenceStore store;
    
    private File tmpFile;
    
    @Mock
    private Connection connection;
    
    @Mock
    private Statement statement;
    
    @Mock
    private ResultSet resultSet;
    
    @Before
    public void setUp() throws Exception {
        tmpFile = File.createTempFile("graph", "");
        store = new HSQLPersistenceStore("dbName", tmpFile.getAbsolutePath());
    }
    
    @After
    public void tearDown() {
        tmpFile.delete();
    }
    
    @Test
    public void testPersistenceStore() {
        assertNotNull(store);
    }

    @Test
    public void testLoadJDBCDriver() {
        store.loadJDBCDriver();
    }
    
    @Test(expected=RuntimeException.class)
    public void testLoadJDBCDriver_throwsException() {
        store = spy(store);
        when(store.getJDBCDriver()).thenReturn("com.does.not.exit.name");
        
        store.loadJDBCDriver();
        
        verify(store).getJDBCDriver();
    }

    @Test
    public void testCreateDatabase() throws SQLException {
        store = spy(store);
        when(store.openConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeUpdate(anyString())).thenReturn(1);
        
        store.createDatabase();
        
        verify(statement).executeUpdate(anyString());
    }
    
    @Test(expected=SQLException.class)
    public void testCreateDatabase_throwsSQLException() throws SQLException {
        store = spy(store);
        when(store.openConnection()).thenThrow(new SQLException());

        store.createDatabase();
     
    }

    @Test
    public void testDoesTableExist_true() throws SQLException {
        store = spy(store);
        when(store.openConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery("SELECT COUNT(*) FROM dbName WHERE SEQUENCE_ID < 100")).thenReturn(resultSet);
        
        assertTrue(store.doesTableExist());
    }
    
    @Test
    public void testDoesTableExist_false() throws SQLException {
        store = spy(store);
        when(store.openConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery("SELECT COUNT(*) FROM dbName WHERE SEQUENCE_ID < 100")).thenThrow(new SQLException());
        
        assertFalse(store.doesTableExist());
        
    }

    @Test
    public void testGetJDBCDriver() {
        assertEquals("org.hsqldb.jdbc.JDBCDriver", store.getJDBCDriver());
    }

    @Test
    public void testOpenConnection() throws SQLException {
        store.openConnection();
    }
    
    @Test
    public void testCloseConnection() throws SQLException {
        store = spy(store);
        when(store.openConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.execute("SHUTDOWN COMPACT;")).thenReturn(Boolean.TRUE);
        
        store.closeConnection();
    }

    @Test
    public void testGetDatabaseName() {
        assertEquals("dbName", store.getDatabaseName());
    }
    
    @Test
    public void testGetConnectionString() {
        assertEquals("jdbc:hsqldb:file:" + tmpFile.getAbsolutePath() + ";shutdown=true", store.getConnectionString());
    }

    @Test
    public void testGetDatabaseFilePath() {
        assertEquals(tmpFile.getAbsolutePath(), store.getDatabaseFilePath());
    }

}
