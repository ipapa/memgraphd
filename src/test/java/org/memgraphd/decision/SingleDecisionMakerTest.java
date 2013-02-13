package org.memgraphd.decision;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.memgraphd.GraphRequestType;
import org.memgraphd.bookkeeper.BookKeeper;
import org.memgraphd.data.Data;
import org.memgraphd.exception.GraphException;
import org.memgraphd.security.GraphRequestContext;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SingleDecisionMakerTest {
    
    private SingleDecisionMaker decisionMaker;
    
    @Mock
    private BookKeeper bookKeeper;
    
    @Mock
    private GraphRequestContext context;
    
    @Mock
    private Data data;
    
    @Mock
    private Decision decision;
    
    @Before
    public void setUp() throws Exception {
        decisionMaker = new SingleDecisionMaker(bookKeeper, 10);
    }

    @Test
    public void testSingleDecisionMaker() {
        assertNotNull(decisionMaker);
    }

    @Test
    public void testDecide_CREATE() throws GraphException {
        when(context.getRequestType()).thenReturn(GraphRequestType.CREATE);
        when(context.getData()).thenReturn(data);
        when(data.getId()).thenReturn("id");
        
        Decision decision = decisionMaker.decide(context);
        
        assertNotNull(decision);
        assertSame(GraphRequestType.CREATE, decision.getRequestType());
        assertSame(data, decision.getData());
        assertEquals("id", decision.getDataId());
        assertNotNull(decision.getSequence());
        assertNotNull(decision.getTime());
        
        verify(context, times(3)).getRequestType();
        verify(context, times(3)).getData();
        verify(data, times(2)).getId();
    }
    
    @Test(expected=GraphException.class)
    public void testDecide_READ() throws GraphException {
        when(context.getRequestType()).thenReturn(GraphRequestType.READ);
       
        decisionMaker.decide(context);
    }
    
    @Test
    public void testDecide_UPDATE() throws GraphException {
        when(context.getRequestType()).thenReturn(GraphRequestType.UPDATE);
        when(context.getData()).thenReturn(data);
        when(data.getId()).thenReturn("id");
        
        Decision decision = decisionMaker.decide(context);
        
        assertNotNull(decision);
        assertSame(GraphRequestType.UPDATE, decision.getRequestType());
        assertSame(data, decision.getData());
        assertEquals("id", decision.getDataId());
        assertNotNull(decision.getSequence());
        assertNotNull(decision.getTime());
        
        verify(context, times(3)).getRequestType();
        verify(context, times(3)).getData();
        verify(data, times(2)).getId();
    }
    
    @Test
    public void testDecide_DELETE() throws GraphException {
        when(context.getRequestType()).thenReturn(GraphRequestType.DELETE);
        when(context.getData()).thenReturn(data);
        when(data.getId()).thenReturn("id");
        
        Decision decision = decisionMaker.decide(context);
        
        assertNotNull(decision);
        assertSame(GraphRequestType.DELETE, decision.getRequestType());
        assertSame(data, decision.getData());
        assertEquals("id", decision.getDataId());
        assertNotNull(decision.getSequence());
        assertNotNull(decision.getTime());
        
        verify(context, times(3)).getRequestType();
        verify(context, times(3)).getData();
        verify(data, times(2)).getId();
    }

    @Test
    public void testReadRange() {
        Sequence start = Sequence.valueOf(1L);
        Sequence end = Sequence.valueOf(2L);
        
        decisionMaker.readRange(start, end);
        
        verify(bookKeeper).readRange(start, end);
    }

    @Test
    public void testLatestDecision() {
        Sequence latest = decisionMaker.latestDecision();
        assertNotNull(latest);
        assertEquals(0L, latest.number());
    }

    @Test
    public void testReverse() {
        Sequence seq = Sequence.valueOf(10L);
        when(decision.getSequence()).thenReturn(seq);
        
        decisionMaker.reverse(decision);
        
        verify(decision).getSequence();
        verify(bookKeeper).wipe(decision);
    }

    @Test
    public void testReverseAll() {
        Sequence seq = Sequence.valueOf(10L);
        when(bookKeeper.lastTransactionId()).thenReturn(seq);
        
        decisionMaker.reverseAll();
        
        assertEquals(seq, decisionMaker.latestDecision());
        verify(bookKeeper).wipeAll();
        verify(bookKeeper).lastTransactionId();
    }

    @Test
    public void testGetReadWriteBatchSize() {
        assertEquals(10L, decisionMaker.getReadWriteBatchSize());
    }

}
