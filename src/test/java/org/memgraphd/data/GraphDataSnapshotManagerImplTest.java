package org.memgraphd.data;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.memgraphd.GraphMappings;
import org.memgraphd.GraphRequestType;
import org.memgraphd.decision.Decision;
import org.memgraphd.decision.DecisionImpl;
import org.memgraphd.decision.DecisionMaker;
import org.memgraphd.decision.Sequence;
import org.memgraphd.exception.GraphException;
import org.memgraphd.memory.MemoryReference;
import org.memgraphd.operation.GraphReader;
import org.memgraphd.operation.GraphWriter;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GraphDataSnapshotManagerImplTest {
    
    private GraphDataSnapshotManagerImpl snapMan;
    
    @Mock
    private GraphReader reader;
    
    @Mock
    private GraphWriter writer;
    
    @Mock
    private GraphMappings mappings;
    
    @Mock
    private DecisionMaker decisionMaker;
    
    @Mock
    private GraphData graphData;
    
    @Mock
    private Data data;
    
    private Decision decision1, decision2, decision3, decision4, decision5;
    
    @Before
    public void setUp() throws Exception {
        snapMan = new GraphDataSnapshotManagerImpl(reader, writer, mappings, decisionMaker);
        
        when(decisionMaker.getReadWriteBatchSize()).thenReturn(5L);
        
        decision1 = new DecisionImpl(Sequence.valueOf(1L), null, GraphRequestType.CREATE, "1", data);
        decision2 = new DecisionImpl(Sequence.valueOf(2L), null, GraphRequestType.DELETE, "2", data);
        decision3 = new DecisionImpl(Sequence.valueOf(3L), null, GraphRequestType.CREATE, "3", data);
        decision4 = new DecisionImpl(Sequence.valueOf(4L), null, GraphRequestType.DELETE, "4", data);
        decision5 = new DecisionImpl(Sequence.valueOf(5L), null, GraphRequestType.CREATE, "5", data);
    }

    @Test
    public void testGraphDataSnapshotManagerImpl() {
        assertNotNull(snapMan);
    }
    
    @Test
    public void testInitialize_throwsGraphException() throws GraphException {
        Sequence seq0 = Sequence.valueOf(0);
        Sequence seq1 = Sequence.valueOf(1);
        
        when(decisionMaker.latestDecision()).thenReturn(seq1);
        when(decisionMaker.readRange(seq0, seq1)).thenReturn(Arrays.asList(decision1, decision2));
        doThrow(new GraphException("")).when(writer).write(decision1);
        doThrow(new GraphException("")).when(writer).delete(decision2);
        
        snapMan.initialize();
        
        verify(decisionMaker).latestDecision();
    }
    
    @Test
    public void testInitialize_BadDecisionWithRequestTypeGET() throws GraphException {
        Sequence seq0 = Sequence.valueOf(0);
        Sequence seq1 = Sequence.valueOf(1);
        Decision d = new DecisionImpl(seq0, null, GraphRequestType.RETRIEVE, "data1", data);
        
        when(decisionMaker.latestDecision()).thenReturn(seq1);
        when(decisionMaker.readRange(seq0, seq1)).thenReturn(Arrays.asList(d));
        
        snapMan.initialize();
        
        verify(decisionMaker).latestDecision();
        verifyZeroInteractions(writer);
    }
    
    @Test
    public void testInitialize_LessThanBatchReadSize() throws GraphException {
        Sequence seq0 = Sequence.valueOf(0);
        Sequence seq1 = Sequence.valueOf(1);
        
        when(decisionMaker.latestDecision()).thenReturn(seq1);
        when(decisionMaker.readRange(seq0, seq1)).thenReturn(Arrays.asList(decision1, decision2));
        
        snapMan.initialize();
        
        verify(decisionMaker).latestDecision();
        verify(writer).write(decision1);
        verify(writer).delete(decision2);
    }
    
    @Test
    public void testInitialize_EqualsBatchReadSize() throws GraphException {
        Sequence seq0 = Sequence.valueOf(0);
        Sequence seq1 = Sequence.valueOf(5);
        
        when(decisionMaker.latestDecision()).thenReturn(seq1);
        when(decisionMaker.readRange(seq0, seq1)).thenReturn(
                Arrays.asList(decision1, decision2, decision3, decision4, decision5));
        
        snapMan.initialize();
        
        verify(decisionMaker).latestDecision();
        verify(writer).write(decision1);
        verify(writer).delete(decision2);
        verify(writer).write(decision3);
        verify(writer).delete(decision4);
        verify(writer).write(decision5);
    }
    
    @Test
    public void testInitialize_GreaterThanBatchReadSize() throws GraphException {
        Sequence seq0 = Sequence.valueOf(0);
        Sequence seq5 = Sequence.valueOf(5);
        Sequence seq10 = Sequence.valueOf(10);
        List<Decision> decisions = Arrays.asList(decision1, decision2, decision3, decision4, decision5);
        
        when(decisionMaker.latestDecision()).thenReturn(seq10);
        when(decisionMaker.readRange(seq0, seq5)).thenReturn(decisions);
        when(decisionMaker.readRange(seq5, seq10)).thenReturn(decisions);
        
        snapMan.initialize();
        
        verify(decisionMaker).latestDecision();
        verify(writer, times(2)).write(decision1);
        verify(writer, times(2)).delete(decision2);
        verify(writer, times(2)).write(decision3);
        verify(writer, times(2)).delete(decision4);
        verify(writer, times(2)).write(decision5);
    }

    @Test
    public void testClear() throws GraphException {
        MemoryReference memRef = MemoryReference.valueOf(1);
        when(mappings.getAllMemoryReferences()).thenReturn(Arrays.asList(memRef));
        when(reader.readReference(memRef)).thenReturn(graphData);
        when(graphData.getData()).thenReturn(data);
        
        snapMan.clear();
        
        verify(writer).delete(graphData);
        verify(decisionMaker).reverseAll();
    }
    
    @Test
    public void testClear_throwsException() throws GraphException {
        MemoryReference memRef = MemoryReference.valueOf(1);
        when(mappings.getAllMemoryReferences()).thenReturn(Arrays.asList(memRef));
        when(reader.readReference(memRef)).thenReturn(graphData);
        doThrow(new GraphException("")).when(writer).delete(graphData);
        when(graphData.getData()).thenReturn(data);
        
        snapMan.clear();

        verify(decisionMaker).reverseAll();
    }

}
