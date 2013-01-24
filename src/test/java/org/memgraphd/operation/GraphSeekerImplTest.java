package org.memgraphd.operation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.memgraphd.GraphMappings;
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
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GraphSeekerImplTest {
    
    private GraphSeekerImpl seeker;
    
    @Mock
    private MemoryOperations memoryAccess;
    
    @Mock
    private GraphMappings mappings;
    
    @Before
    public void setUp() throws Exception {
        seeker = new GraphSeekerImpl(memoryAccess, mappings);
    }

    @Test
    public void testGraphSeekerImpl() {
        assertNotNull(seeker);
        assertSame(memoryAccess, seeker.getMemoryAccess());
    }
    
    @Test
    public void testSeekByIdString() {
        MemoryReference ref = MemoryReference.valueOf(1);
        when(mappings.getById("id")).thenReturn(ref);
        
        assertEquals(ref, seeker.seekById("id"));
        
        verify(mappings).getById("id");
    }

    @Test
    public void testSeekBySequenceSequence() {
        Sequence seq = Sequence.valueOf(1L);
        MemoryReference ref = MemoryReference.valueOf(1);
        when(mappings.getBySequence(seq)).thenReturn(ref);
        
        assertEquals(ref, seeker.seekBySequence(seq));
        
        verify(mappings).getBySequence(seq);
    }

    @Test
    public void testSeekByIdStringArray_emptyList() {
        MemoryReference[] result = seeker.seekById(new String[] {});
        
        assertNotNull(result);
        assertEquals(0, result.length);
    }
    
    @Test
    public void testSeekByIdStringArray() {
        MemoryReference ref = MemoryReference.valueOf(1);
        when(mappings.getById("id1")).thenReturn(ref);
        when(mappings.getById("id2")).thenReturn(null);
        
        MemoryReference[] result = seeker.seekById(new String[] { "id1", "id2"});
        
        assertNotNull(result);
        assertEquals(2, result.length);
        assertSame(ref, result[0]);
        assertNull(result[1]);
    }

    @Test
    public void testSeekBySequenceSequenceArray_emptyList() {
        MemoryReference[] result = seeker.seekBySequence(new Sequence[] {});
        
        assertNotNull(result);
        assertEquals(0, result.length);
    }
    
    @Test
    public void testSeekBySequenceSequenceArray() {
        MemoryReference ref = MemoryReference.valueOf(1);
        Sequence seq1 = Sequence.valueOf(1L);
        Sequence seq2 = Sequence.valueOf(2L);
        when(mappings.getBySequence(seq1)).thenReturn(ref);
        when(mappings.getBySequence(seq2)).thenReturn(null);
        
        MemoryReference[] result = seeker.seekBySequence(new Sequence[] { seq1, seq2});
        
        assertNotNull(result);
        assertEquals(2, result.length);
        assertSame(ref, result[0]);
        assertNull(result[1]);
    }

}
