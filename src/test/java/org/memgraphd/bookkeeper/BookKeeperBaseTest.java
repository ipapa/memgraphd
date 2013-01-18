package org.memgraphd.bookkeeper;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BookKeeperBaseTest {
    private BookKeeperBase base;
    
    private static final String THREAD_NAME = "some thread name";
    
    private static final String DB_NAME = "some db name";
    
    @Mock
    private Connection connection;
    
    @Mock
    private Statement statement;
    
    @Mock
    private PersistenceStore persistenceStore;
    
    @Before
    public void setUp() throws Exception {
        base = new BookKeeperReader(THREAD_NAME, persistenceStore);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeUpdate("COMMIT;")).thenReturn(1);
        
        when(persistenceStore.getDatabaseName()).thenReturn(DB_NAME);
        when(persistenceStore.openConnection()).thenReturn(connection);
    }

    @Test
    public void testBookKeeperBase() {
        assertNotNull(base);
    }

    @Test
    public void testGetThreadName() {
        assertEquals(THREAD_NAME, base.getThreadName());
    }

    @Test
    public void testGetDbName() {
        assertEquals(DB_NAME, base.getPersistenceStore().getDatabaseName());
    }

    @Test
    public void testGetConnection() throws SQLException {
        assertSame(connection, base.getPersistenceStore().openConnection());
    }

    @Test
    public void testCompleted() throws SQLException {
        base.completed();
        assertTrue(base.isSuccess());
        verify(connection).createStatement();
        verify(statement).executeUpdate("COMMIT;");
    }
    
    @Test(expected=SQLException.class)
    public void testCompleted_throwsException() throws SQLException {
        doThrow(new SQLException()).when(connection).createStatement();
        base.completed();
        assertTrue(base.isSuccess());
        verify(connection).createStatement().executeUpdate("COMMIT;");
    }

    @Test
    public void testError() {
        assertFalse(base.isFailure());
        base.error();
        assertTrue(base.isFailure());
    }

    @Test
    public void testHasRun_success() throws SQLException {
        assertFalse(base.hasRun());
        base.completed();
        assertTrue(base.hasRun());
    }
    
    @Test
    public void testHasRun_failure() throws SQLException {
        assertFalse(base.hasRun());
        base.error();
        assertTrue(base.hasRun());
    }

    @Test
    public void testIsSuccess() throws SQLException {
        assertFalse(base.isSuccess());
        base.completed();
        assertTrue(base.isSuccess());
    }

    @Test
    public void testIsFailure() {
        assertFalse(base.isFailure());
        base.error();
        assertTrue(base.isFailure());
    }

}
