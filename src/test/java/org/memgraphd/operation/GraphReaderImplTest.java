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
        assertNull(reader.read("id"));
        
        verify(seeker).seekById("id");
        verifyZeroInteractions(memoryAccess);
    }
    
    @Test
    public void testReadId() {
        MemoryReference ref = MemoryReference.valueOf(1);
        when(seeker.seekById("id")).thenReturn(ref);
        when(memoryAccess.readGraph(ref)).thenReturn(gData);
        
        assertSame(gData, reader.read("id"));
        
        verify(seeker).seekById("id");
        verify(memoryAccess).readGraph(ref);
    }
    
    @Test
    public void testReadSequence_NullReference() {
        Sequence seq = Sequence.valueOf(1L);
        when(seeker.seekBySequence(seq)).thenReturn(null);
        
        assertNull(reader.read(seq));
        
        verify(seeker).seekBySequence(seq);
        verifyZeroInteractions(memoryAccess);
    }
    
    @Test
    public void testReadSequence() {
        Sequence seq = Sequence.valueOf(1L);
        MemoryReference ref = MemoryReference.valueOf(1);
        when(seeker.seekBySequence(seq)).thenReturn(ref);
        when(memoryAccess.readGraph(ref)).thenReturn(gData);
        
        assertSame(gData, reader.read(seq));
        
        verify(seeker).seekBySequence(seq);
        verify(memoryAccess).readGraph(ref);
    }

    @Test
    public void testReadReference_nullRef() {
        when(memoryAccess.readGraph(null)).thenReturn(null);
        assertNull(reader.read((MemoryReference)null));
        verify(memoryAccess).readGraph(null);
    }
    
    @Test
    public void testReadReference() {
        MemoryReference ref = MemoryReference.valueOf(1);
        when(memoryAccess.readGraph(ref)).thenReturn(gData);
        
        assertEquals(gData, reader.read(ref));
        
        verify(memoryAccess).readGraph(ref);
    }
    
}
