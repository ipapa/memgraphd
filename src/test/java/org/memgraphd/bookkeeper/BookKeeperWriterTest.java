package org.memgraphd.bookkeeper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.memgraphd.GraphRequestType;
import org.memgraphd.decision.Decision;
import org.memgraphd.decision.Sequence;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;

import static org.mockito.Matchers.anyString;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BookKeeperWriterTest {
    
    private BookKeeperWriter writer;
    
    @Mock
    private Connection connection;

    private Set<Decision> decisions;
    
    @Mock
    private Decision d1, d2;
    
    @Mock
    private Statement statement;
    
    @Mock
    private PreparedStatement preparedStatement;
    
    @Mock
    private PersistenceStore persistenceStore;
    
    @Before
    public void setUp() throws Exception {
        decisions = new HashSet<Decision>();
        writer = new BookKeeperWriter("thread-name", persistenceStore, decisions);
        when(persistenceStore.openConnection()).thenReturn(connection);
    }

    @Test
    public void testBookKeeperWriter() {
        assertNotNull(writer);
    }

    @Test
    public void testRun_decisionsEmpty() {
        writer.run();
        verifyNoMoreInteractions(connection);
    }
    
    @Test
    public void testRun() throws SQLException {
        decisions.add(d1);
        decisions.add(d2);
        
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeUpdate("COMMIT;")).thenReturn(1);
        
        when(d1.getSequence()).thenReturn(Sequence.valueOf(1));
        when(d2.getSequence()).thenReturn(Sequence.valueOf(2));
        when(d1.getTime()).thenReturn(new DateTime());
        when(d2.getTime()).thenReturn(new DateTime());
        when(d1.getRequestType()).thenReturn(GraphRequestType.CREATE);
        when(d2.getRequestType()).thenReturn(GraphRequestType.DELETE);
        
        when(preparedStatement.executeBatch()).thenReturn(null);
        
        writer.run();

        verify(preparedStatement, times(2)).addBatch();
        verify(preparedStatement).executeBatch();
        verify(statement).executeUpdate("COMMIT;");
    }
    
    @Test(expected=RuntimeException.class)
    public void testRun_throwsExeption() throws SQLException {
        decisions.add(d1);
        decisions.add(d2);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException());
        
        writer.run();
    }

}
