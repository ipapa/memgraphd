package org.memgraphd.bookkeeper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.memgraphd.data.Data;
import org.memgraphd.data.ReadWriteData;
import org.memgraphd.decision.Decision;
import org.memgraphd.decision.Sequence;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.common.collect.ImmutableSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import static org.mockito.Matchers.anyString;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractBookKeeperTest {
    
    private HSQLBookKeeper keeper;
    
    @Mock
    private Decision decision;
    
    @Mock 
    private Data data;
    
    @Mock
    private Connection connection;
    
    @Mock
    private Statement statement;
    
    @Mock 
    private ResultSet resultSet;
    
    @Mock
    private Decision d1, d2, d3, d4, d5, d6;
    
    @Mock
    private PersistenceStore persistenceStore;
    
    @Mock
    private BookKeeperReader bookKeeperReader;
    
    @Before
    public void setUp() throws Exception {
        keeper =  new HSQLBookKeeper(persistenceStore, 5, 5);
        data = new ReadWriteData("someId", new DateTime(), new DateTime());
        when(decision.getData()).thenReturn(data);
    }
    
    @Test
    public void testAbstractBookKeeper() {
        assertNotNull(keeper);
    }

    @Test
    public void testOpenBook() {
        assertFalse(keeper.isBookOpen());
        keeper.openBook();
        assertTrue(keeper.isBookOpen());
    }
    
    @Test
    public void testOpenBook_alreadyOpened() {
        testOpenBook();
        keeper.openBook();
        assertTrue(keeper.isBookOpen());
    }

    @Test(expected=RuntimeException.class)
    public void testRecord_bookNotOpened() {
        keeper.record(decision);
    }
    
    @Test
    public void testRecord() {
        keeper.openBook();
        keeper.record(decision);
    }

    @Test
    public void testCloseBook() {
        assertFalse(keeper.isBookOpen());
        keeper.openBook();
        keeper.closeBook();
        assertFalse(keeper.isBookOpen());
    }
    
    @Test
    public void testCloseBook_bookNotOpened() {
        keeper.closeBook();
        assertFalse(keeper.isBookOpen());
    }
    
    @Test
    public void testCloseBook_bookAlreadyClosed() {
        testCloseBook();
        keeper.closeBook();
        assertFalse(keeper.isBookOpen());
    }
    
    @Test
    public void testCloseBook_throwsSQLException() throws SQLException {
        assertFalse(keeper.isBookOpen());
        keeper.openBook();
        doThrow(new SQLException()).when(persistenceStore).closeConnection();
        
        keeper.closeBook();
        
        assertFalse(keeper.isBookOpen());
    }

    @Test
    public void testIsBookOpen() {
        assertFalse(keeper.isBookOpen());
        keeper.openBook();
        assertTrue(keeper.isBookOpen());
        keeper.closeBook();
        assertFalse(keeper.isBookOpen());
    }
    
    @Test(expected=RuntimeException.class)
    public void testLastTransactionId_bookNotOpen() {
        keeper.lastTransactionId();
    }
    
    @Test
    public void testLastTransactionId_bookOpen() throws SQLException {
        keeper.openBook();
        when(persistenceStore.openConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(persistenceStore.getDatabaseName()).thenReturn("dbName");
        when(statement.executeQuery("SELECT MAX(SEQUENCE_ID) FROM dbName;")).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        
        Sequence seq = keeper.lastTransactionId();
        
        assertNotNull(seq);
        assertEquals(0, seq.number());
    }
    
    @Test
    public void testLastTransactionId() throws SQLException {
        keeper.openBook();
        HSQLBookKeeper spyKeeper = spy(keeper);
        when(persistenceStore.openConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getLong(1)).thenReturn(10L);
        
        Sequence lastTran = spyKeeper.lastTransactionId();
        assertNotNull(lastTran);
        assertEquals(10L, lastTran.number());
    }
    
    @Test
    public void testLastTransactionId_noResultReturnsZero() throws SQLException {
        keeper.openBook();
        HSQLBookKeeper spyKeeper = spy(keeper);
        when(persistenceStore.openConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        
        when(resultSet.next()).thenReturn(false);
        
        Sequence lastTran = spyKeeper.lastTransactionId();
        assertNotNull(lastTran);
        assertEquals(0, lastTran.number());
    }
    
    @Test(expected=RuntimeException.class)
    public void testLastTransactionId_throwsSQLException() throws SQLException {
        keeper.openBook();
        HSQLBookKeeper spyKeeper = spy(keeper);
        when(persistenceStore.openConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(anyString())).thenThrow(new SQLException());
        
        spyKeeper.lastTransactionId();
    }

    @Test
    public void testReadRange() throws SQLException {
        Sequence seq1 = Sequence.valueOf(1);
        Sequence seq2 = Sequence.valueOf(2);
        
        ReflectionTestUtils.setField(keeper, "reader", bookKeeperReader);
        
        when(bookKeeperReader.readRange(seq1, seq2)).thenReturn(new ArrayList<Decision>());
        
        keeper.openBook();
        
        List<Decision> decisions = keeper.readRange(seq1, seq2);
        
        assertTrue(decisions.isEmpty());
        
        verify(bookKeeperReader).readRange(seq1, seq2);
    }
    
    @Test(expected=RuntimeException.class)
    public void testReadRange_bookNotOpened() {
        keeper.readRange(Sequence.valueOf(1), Sequence.valueOf(2));
    }
    
    @Test(expected=RuntimeException.class)
    public void testReadRange_throwsException() throws SQLException {
        keeper.openBook();
        
        Sequence seq1 = Sequence.valueOf(1);
        Sequence seq2 = Sequence.valueOf(2);
        BookKeeperReader reader = Mockito.mock(BookKeeperReader.class);
        ReflectionTestUtils.setField(keeper, "reader", reader);
        
        when(reader.readRange(seq1, seq2)).thenThrow(new SQLException());
        
        keeper.readRange(seq1, seq2);
    }
    
    @Test
    public void testIsFlushToDiskTime_bufferFull() {        
        Set<Decision> decisions = ImmutableSet.of(d1, d2, d3, d4, d5);
        ReflectionTestUtils.setField(keeper, "buffer", decisions);
        ReflectionTestUtils.setField(keeper, "lastTimeFlushedToDisk", new AtomicLong(System.currentTimeMillis() - 2));
        
        assertTrue(keeper.isFlushToDiskTime());
    }
    
    @Test
    public void testIsFlushToDiskTime_bufferOverFlown() {
        
        Set<Decision> decisions = ImmutableSet.of(d1, d2, d3, d4, d5, d6);
        ReflectionTestUtils.setField(keeper, "buffer", decisions);
        ReflectionTestUtils.setField(keeper, "lastTimeFlushedToDisk", new AtomicLong(System.currentTimeMillis() - 2));
        
        assertTrue(keeper.isFlushToDiskTime());
    }
    
    @Test
    public void testIsFlushToDiskTime_bufferNotFilled() {
        
        Set<Decision> decisions = ImmutableSet.of(d1, d2, d3, d4);
        ReflectionTestUtils.setField(keeper, "buffer", decisions);
        ReflectionTestUtils.setField(keeper, "lastTimeFlushedToDisk", new AtomicLong(System.currentTimeMillis() - 2));
        
        assertFalse(keeper.isFlushToDiskTime());
    }
    
    @Test
    public void testIsFlushToDiskTime_timeToFlushToDisk() {
        
        Set<Decision> decisions = ImmutableSet.of();
        ReflectionTestUtils.setField(keeper, "buffer", decisions);
        ReflectionTestUtils.setField(keeper, "lastTimeFlushedToDisk", new AtomicLong(System.currentTimeMillis() - 6));
        
        assertTrue(keeper.isFlushToDiskTime());
    }
    
    @Test
    public void testIsFlushToDiskTime_notTimeToFlushToDisk() {
        
        Set<Decision> decisions = ImmutableSet.of();
        ReflectionTestUtils.setField(keeper, "buffer", decisions);
        ReflectionTestUtils.setField(keeper, "lastTimeFlushedToDisk", new AtomicLong(System.currentTimeMillis() - 5));
        
        assertFalse(keeper.isFlushToDiskTime());
    }
    
    @Test
    public void testSplitDecisionsIntoBatches() {
        Set<Decision> decisions = ImmutableSet.of(d1, d2, d3, d4, d5, d6);
        
        List<Set<Decision>> result = keeper.splitDecisionsIntoBatches(decisions);
        
        assertNotNull(result);
        assertEquals(2, result.size());
        Set<Decision> set1 = result.get(0);
        Set<Decision> set2 = result.get(1);
        
        assertNotNull(set1);
        assertNotNull(set2);
        assertTrue(set1.contains(d1));
        assertTrue(set1.contains(d2));
        assertTrue(set1.contains(d3));
        assertTrue(set1.contains(d4));
        assertTrue(set1.contains(d5));
        
        assertTrue(set2.contains(d6));
    }

}
