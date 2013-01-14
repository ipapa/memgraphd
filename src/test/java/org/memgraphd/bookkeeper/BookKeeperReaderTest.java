package org.memgraphd.bookkeeper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.memgraphd.GraphRequestType;
import org.memgraphd.data.DataImpl;
import org.memgraphd.decision.Decision;
import org.memgraphd.decision.Sequence;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import static org.mockito.Matchers.anyString;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BookKeeperReaderTest {
    
    private BookKeeperReader reader;
    
    @Mock
    private Connection connection;
    
    @Mock
    private PreparedStatement statement;
    
    @Mock
    private ResultSet resultSet;
    
    @Before
    public void setUp() throws Exception {
        reader = new BookKeeperReader("thread name", "db name", connection);
    }

    @Test
    public void testBookKeeperReader() {
        assertNotNull(reader);
    }

    @Test
    public void testReadRange() throws Exception {
       Sequence seq_start = Sequence.valueOf(1L);
       Sequence seq_end = Sequence.valueOf(2L);
       when(connection.prepareStatement(anyString())).thenReturn(statement);
       when(statement.executeQuery()).thenReturn(resultSet);
       
       when(resultSet.next()).thenReturn(true).thenReturn(false);
       when(resultSet.getLong("SEQUENCE_ID")).thenReturn(1L);
       when(resultSet.getTimestamp("DECISION_TIME")).thenReturn(new Timestamp(System.currentTimeMillis()));
       when(resultSet.getString("REQUEST_TYPE")).thenReturn("PUT");
       when(resultSet.getString("DATA_ID")).thenReturn("id");
       when(resultSet.getObject("DATA")).thenReturn(new DataImpl("id", null, null));
       
       List<Decision> decisions = reader.readRange(seq_start, seq_end);
       assertNotNull(decisions);
       assertEquals(1, decisions.size());
       Decision d = decisions.get(0);
       
       assertNotNull(d);
       assertEquals(1L, d.getSequence().number());
       assertEquals(GraphRequestType.PUT, d.getRequestType());
       assertNotNull(d.getTime());
       assertEquals("id", d.getDataId());
       assertNotNull(d.getData());
       
       verify(statement).setLong(1, seq_start.number());
       verify(statement).setLong(2, seq_end.number());
       verify(resultSet, times(2)).next();
       
    }
    
    @Test(expected=SQLException.class)
    public void testReadRange_throwsException() throws SQLException {

       when(connection.prepareStatement(anyString())).thenThrow(new SQLException());
       
       reader.readRange(null, null);
       
    }

}
