package org.memgraphd.operation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.memgraphd.data.GraphData;
import org.memgraphd.decision.Sequence;
import org.memgraphd.memory.MemoryBlock;
import org.memgraphd.memory.MemoryReference;
import org.memgraphd.memory.operation.MemoryOperations;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GraphFilterImplTest {
    
    private GraphFilterImpl filter;
    
    @Mock
    private MemoryOperations memoryAccess;
    
    @Mock
    private MemoryBlock memoryBlock;
    
    @Mock
    private GraphReader reader;
    
    @Mock
    private GraphData gData1, gData2, gData3;
    
    @Before
    public void setUp() throws Exception {
        filter = new GraphFilterImpl(memoryAccess, reader);
    }

    @Test
    public void testGraphFilterImpl() {
        assertNotNull(filter);
    }

    @Test
    public void testFilterBy() {
        MemoryReference ref1 = MemoryReference.valueOf(1);
        MemoryReference ref2 = MemoryReference.valueOf(2);
        MemoryReference ref3 = MemoryReference.valueOf(3);
        
        when(reader.read(ref1)).thenReturn(gData1);
        when(reader.read(ref2)).thenReturn(gData2);
        when(reader.read(ref3)).thenReturn(gData3);
        
        when(memoryBlock.startsWith()).thenReturn(ref1);
        when(memoryBlock.endsWith()).thenReturn(ref3);
        
        GraphData[] result = filter.filterBy(memoryBlock);
        
        assertNotNull(result);
        assertEquals(3, result.length);
        assertSame(gData1, result[0]);
        assertSame(gData2, result[1]);
        assertSame(gData3, result[2]);
        
        verify(reader).read(ref1);
        verify(reader).read(ref2);
        verify(reader).read(ref3);
    }

    @Test
    public void testFilterByRangeMemoryReferenceMemoryReference() {
        MemoryReference ref1 = MemoryReference.valueOf(1);
        MemoryReference ref2 = MemoryReference.valueOf(2);
        MemoryReference ref3 = MemoryReference.valueOf(3);
        
        when(reader.read(ref1)).thenReturn(gData1);
        when(reader.read(ref2)).thenReturn(gData2);
        when(reader.read(ref3)).thenReturn(gData3);
        
        GraphData[] result = filter.filterByRange(ref1, ref3);
        
        assertNotNull(result);
        assertEquals(3, result.length);
        assertSame(gData1, result[0]);
        assertSame(gData2, result[1]);
        assertSame(gData3, result[2]);
        
        verify(reader).read(ref1);
        verify(reader).read(ref2);
        verify(reader).read(ref3);
    }

    @Test
    public void testFilterByRangeSequenceSequence() {
        Sequence seq1 = Sequence.valueOf(1L);
        Sequence seq2 = Sequence.valueOf(2L);
        Sequence seq3 = Sequence.valueOf(3L);
        
        when(reader.read(seq1)).thenReturn(gData1);
        when(reader.read(seq2)).thenReturn(gData2);
        when(reader.read(seq3)).thenReturn(gData3);
        
        GraphData[] result = filter.filterByRange(seq1, seq3);
        
        assertNotNull(result);
        assertEquals(3, result.length);
        assertSame(gData1, result[0]);
        assertSame(gData2, result[1]);
        assertSame(gData3, result[2]);
        
        verify(reader).read(seq1);
        verify(reader).read(seq2);
        verify(reader).read(seq3);
    }

}
