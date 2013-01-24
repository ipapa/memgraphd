package org.memgraphd.operation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.memgraphd.data.GraphData;
import org.memgraphd.decision.Sequence;
import org.memgraphd.memory.MemoryReference;
import org.memgraphd.memory.operation.MemoryOperations;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GraphReaderImplTest {
    
    private GraphReaderImpl reader;
    
    @Mock
    private MemoryOperations memoryAccess;
    
    @Mock
    private GraphSeeker seeker;
    
    @Mock
    private GraphData gData;
    
    @Before
    public void setUp() throws Exception {
        reader = new GraphReaderImpl(memoryAccess, seeker);
    }

    @Test
    public void testGraphReaderImpl() {
        assertNotNull(reader);
        assertSame(memoryAccess, reader.getMemoryAccess());
    }

    @Test
    public void testReadId_NullReference() {
        when(seeker.seekById("id")).thenReturn(null);
        assertNull(reader.readId("id"));
        
        verify(seeker).seekById("id");
        verifyZeroInteractions(memoryAccess);
    }
    
    @Test
    public void testReadId() {
        MemoryReference ref = MemoryReference.valueOf(1);
        when(seeker.seekById("id")).thenReturn(ref);
        when(memoryAccess.read(ref)).thenReturn(gData);
        
        assertSame(gData, reader.readId("id"));
        
        verify(seeker).seekById("id");
        verify(memoryAccess).read(ref);
    }
    
    @Test
    public void testReadSequence_NullReference() {
        Sequence seq = Sequence.valueOf(1L);
        when(seeker.seekBySequence(seq)).thenReturn(null);
        
        assertNull(reader.readSequence(seq));
        
        verify(seeker).seekBySequence(seq);
        verifyZeroInteractions(memoryAccess);
    }
    
    @Test
    public void testReadSequence() {
        Sequence seq = Sequence.valueOf(1L);
        MemoryReference ref = MemoryReference.valueOf(1);
        when(seeker.seekBySequence(seq)).thenReturn(ref);
        when(memoryAccess.read(ref)).thenReturn(gData);
        
        assertSame(gData, reader.readSequence(seq));
        
        verify(seeker).seekBySequence(seq);
        verify(memoryAccess).read(ref);
    }

    @Test
    public void testReadIds_emptyList() {
        GraphData[] result = reader.readIds(new String[] {});
        assertNotNull(result);
        assertEquals(0, result.length);
    }
    
    @Test
    public void testReadIds() {
        MemoryReference ref = MemoryReference.valueOf(1);
        when(seeker.seekById("id")).thenReturn(ref);
        when(seeker.seekById("id2")).thenReturn(null);
        when(memoryAccess.read(ref)).thenReturn(gData);
        
        GraphData[] result = reader.readIds(new String[] { "id", "id2"});
        
        assertNotNull(result);
        assertEquals(2, result.length);
        assertSame(gData, result[0]);
        assertNull(result[1]);
    }
    
    @Test
    public void testReadSequences_emptyList() {
        GraphData[] result = reader.readSequences(new Sequence[] {});
        assertNotNull(result);
        assertEquals(0, result.length);
    }
    
    @Test
    public void testReadSequences() {
        Sequence seq1 = Sequence.valueOf(1L);
        Sequence seq2 = Sequence.valueOf(2L);
        MemoryReference ref = MemoryReference.valueOf(1);
        when(seeker.seekBySequence(seq1)).thenReturn(ref);
        when(seeker.seekBySequence(seq2)).thenReturn(null);
        when(memoryAccess.read(ref)).thenReturn(gData);
        
        GraphData[] result = reader.readSequences(new Sequence[] { seq1, seq2});
        
        assertNotNull(result);
        assertEquals(2, result.length);
        assertSame(gData, result[0]);
        assertNull(result[1]);
    }

    @Test
    public void testReadReference_nullRef() {
        when(memoryAccess.read(null)).thenReturn(null);
        assertNull(reader.readReference(null));
        verify(memoryAccess).read(null);
    }
    
    @Test
    public void testReadReference() {
        MemoryReference ref = MemoryReference.valueOf(1);
        when(memoryAccess.read(ref)).thenReturn(gData);
        
        assertEquals(gData, reader.readReference(ref));
        
        verify(memoryAccess).read(ref);
    }

    @Test
    public void testReadReferences_emptyList() {
        GraphData[] result = reader.readReferences(new MemoryReference[] {});
        assertNotNull(result);
        assertEquals(0, result.length);
    }
    
    @Test
    public void testReadReferences() {
        MemoryReference ref1 = MemoryReference.valueOf(1);
        MemoryReference ref2 = MemoryReference.valueOf(2);
        when(memoryAccess.read(ref1)).thenReturn(gData);
        when(memoryAccess.read(ref2)).thenReturn(null);
        
        GraphData[] result = reader.readReferences(new MemoryReference[] { ref1, ref2});
        
        assertNotNull(result);
        assertEquals(2, result.length);
        assertSame(gData, result[0]);
        assertNull(result[1]);
    }

    @Test
    public void testReadGraphId() {
        MemoryReference ref = MemoryReference.valueOf(1);
        when(seeker.seekById("id")).thenReturn(ref);
        when(memoryAccess.readGraph(ref)).thenReturn(gData);
        
        assertSame(gData, reader.readGraph("id"));
        
        verify(seeker).seekById("id");
        verify(memoryAccess).readGraph(ref);
    }
    
    @Test
    public void testReadGraphId_NullReference() {
        when(seeker.seekById("id")).thenReturn(null);
        assertNull(reader.readGraph("id"));
        
        verify(seeker).seekById("id");
        verifyZeroInteractions(memoryAccess);
    }
    
    @Test
    public void testReadGraphSequence() {
        Sequence seq = Sequence.valueOf(1L);
        MemoryReference ref = MemoryReference.valueOf(1);
        when(seeker.seekBySequence(seq)).thenReturn(ref);
        when(memoryAccess.readGraph(ref)).thenReturn(gData);
        
        assertSame(gData, reader.readGraph(seq));
        
        verify(seeker).seekBySequence(seq);
        verify(memoryAccess).readGraph(ref);
    }

    @Test
    public void testReadGraphIds_emptyList() {
        GraphData[] result = reader.readGraph(new String[] {});
        assertNotNull(result);
        assertEquals(0, result.length);
    }
    
    @Test
    public void testReadGraphIds() {
        MemoryReference ref = MemoryReference.valueOf(1);
        when(seeker.seekById("id")).thenReturn(ref);
        when(seeker.seekById("id2")).thenReturn(null);
        when(memoryAccess.readGraph(ref)).thenReturn(gData);
        
        GraphData[] result = reader.readGraph(new String[] { "id", "id2"});
        
        assertNotNull(result);
        assertEquals(2, result.length);
        assertSame(gData, result[0]);
        assertNull(result[1]);
    }
    
    @Test
    public void testReadGraphSequences_emptyList() {
        GraphData[] result = reader.readGraph(new Sequence[] {});
        assertNotNull(result);
        assertEquals(0, result.length);
    }
    
    @Test
    public void testReadGraphSequences() {
        Sequence seq1 = Sequence.valueOf(1L);
        Sequence seq2 = Sequence.valueOf(2L);
        MemoryReference ref = MemoryReference.valueOf(1);
        when(seeker.seekBySequence(seq1)).thenReturn(ref);
        when(seeker.seekBySequence(seq2)).thenReturn(null);
        when(memoryAccess.readGraph(ref)).thenReturn(gData);
        
        GraphData[] result = reader.readGraph(new Sequence[] { seq1, seq2});
        
        assertNotNull(result);
        assertEquals(2, result.length);
        assertSame(gData, result[0]);
        assertNull(result[1]);
    }
    
    @Test
    public void testReadGraphReference() {
        MemoryReference ref = MemoryReference.valueOf(1);
        when(memoryAccess.readGraph(ref)).thenReturn(gData);
        
        assertEquals(gData, reader.readGraph(ref));
        
        verify(memoryAccess).readGraph(ref);
    }

    @Test
    public void testReadGraphReferences_emptyList() {
        GraphData[] result = reader.readGraph(new MemoryReference[] {});
        assertNotNull(result);
        assertEquals(0, result.length);
    }
    
    @Test
    public void testReadGraphReferences() {
        MemoryReference ref1 = MemoryReference.valueOf(1);
        MemoryReference ref2 = MemoryReference.valueOf(2);
        when(memoryAccess.readGraph(ref1)).thenReturn(gData);
        when(memoryAccess.readGraph(ref2)).thenReturn(null);
        
        GraphData[] result = reader.readGraph(new MemoryReference[] { ref1, ref2});
        
        assertNotNull(result);
        assertEquals(2, result.length);
        assertSame(gData, result[0]);
        assertNull(result[1]);
    }
}
