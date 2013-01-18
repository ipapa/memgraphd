package org.memgraphd.bookkeeper;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.memgraphd.decision.Decision;
import org.memgraphd.decision.Sequence;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableSet;

import static org.junit.Assert.assertNotNull;

import static org.mockito.Matchers.any;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HSQLBookKeeperTest {
    private HSQLBookKeeper keeper;
    
    @Mock
    private ExecutorService executor;
    
    @Mock
    private Decision d1, d2, d3, d4, d5;
    
    @Mock
    private Connection connection;
    
    @Mock
    private Statement statement;
    
    @Mock
    private PersistenceStore persistenceStore;
    
    private List<Set<Decision>> listOfDecions;
    
    private File tmpFile;
    
    @Before
    public void setUp() throws Exception {
        tmpFile = File.createTempFile("graph", "");
        keeper = new HSQLBookKeeper(persistenceStore, 3L, 5L);
        listOfDecions = new ArrayList<Set<Decision>>();
        listOfDecions.add(ImmutableSet.of(d1,d2,d3));
        listOfDecions.add(ImmutableSet.of(d4, d5));
        
        when(persistenceStore.getDatabaseName()).thenReturn("dbName");
    }
    
    @After
    public void tearDown() {
        tmpFile.delete();
    }

    @Test
    public void testHSQLBookKeeper() {
        assertNotNull(keeper);
    }
    
    @Test
    public void testRun_notTimeToFlushToDisk() {
        HSQLBookKeeper spyKeeper = spy(keeper);
        when(spyKeeper.isFlushToDiskTime()).thenReturn(false);
        
        spyKeeper.run();
        
        verifyZeroInteractions(executor);
        verify(spyKeeper).isFlushToDiskTime();
    }
    
    @Test
    public void testRun_timeToFlushToDisk() {
        HSQLBookKeeper spyKeeper = spy(keeper);
        when(spyKeeper.isFlushToDiskTime()).thenReturn(true);
        Set<Decision> set = ImmutableSet.of(d1,d2,d3,d4,d5);
        when(spyKeeper.swapBuffer()).thenReturn(set);
        when(spyKeeper.splitDecisionsIntoBatches(set)).thenReturn(listOfDecions);
        when(spyKeeper.getExecutor()).thenReturn(executor);

        spyKeeper.run();

        verify(spyKeeper).isFlushToDiskTime();
        verify(executor, times(2)).execute(any(BookKeeperWriter.class));
    }

    @Test
    public void testWipe() throws SQLException {
        HSQLBookKeeper spyKeeper = spy(keeper);
        when(persistenceStore.openConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(d1.getSequence()).thenReturn(Sequence.valueOf(1));
        when(statement.executeUpdate("DELETE FROM dbName WHERE SEQUENCE_ID=1;")).thenReturn(1);
        when(statement.executeUpdate("COMMIT;")).thenReturn(1);
        
        spyKeeper.wipe(d1);
        
        verify(statement).executeUpdate("DELETE FROM dbName WHERE SEQUENCE_ID=1;");
        verify(statement).executeUpdate("COMMIT;");
    }
    
    @Test(expected=RuntimeException.class)
    public void testWipe_throwsSQLException() throws SQLException {
        HSQLBookKeeper spyKeeper = spy(keeper);
        when(d1.getSequence()).thenReturn(Sequence.valueOf(1));
        when(persistenceStore.openConnection()).thenReturn(connection);
        when(connection.createStatement()).thenThrow(new SQLException());
        
        spyKeeper.wipe(d1);
    }

    @Test
    public void testWipeAll() throws SQLException {
        HSQLBookKeeper spyKeeper = spy(keeper);
        when(persistenceStore.openConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeUpdate("DELETE FROM dbName;")).thenReturn(1);
        when(statement.executeUpdate("COMMIT;")).thenReturn(1);
        
        spyKeeper.wipeAll();
        
        verify(statement).executeUpdate("DELETE FROM dbName;");
        verify(statement).executeUpdate("COMMIT;");
    }
    
    @Test(expected=RuntimeException.class)
    public void testWipeAll_throwsSQLException() throws SQLException {
        HSQLBookKeeper spyKeeper = spy(keeper);
        when(persistenceStore.openConnection()).thenReturn(connection);
        when(connection.createStatement()).thenThrow(new SQLException());
        
        spyKeeper.wipeAll();
    }

}
