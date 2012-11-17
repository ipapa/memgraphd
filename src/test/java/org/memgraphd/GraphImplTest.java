package org.memgraphd;

import java.lang.reflect.Constructor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.memgraphd.data.Data;
import org.memgraphd.data.GraphData;
import org.memgraphd.data.GraphDataSnapshotManager;
import org.memgraphd.data.relationship.DataMatchmaker;
import org.memgraphd.decision.Sequence;
import org.memgraphd.exception.GraphException;
import org.memgraphd.memory.MemoryReference;
import org.memgraphd.memory.MemoryStats;
import org.memgraphd.memory.operation.MemoryOperations;
import org.memgraphd.operation.GraphFilter;
import org.memgraphd.operation.GraphReader;
import org.memgraphd.operation.GraphSeeker;
import org.memgraphd.operation.GraphWriter;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GraphImplTest {
    
    private GraphImpl graph;

    private GraphSupervisor supervisor;
    
    @Mock
    private GraphData gData;
    
    @Mock
    private Data data;
    
    @Mock
    private GraphWriter writer;
    
    @Mock
    private GraphReader reader;
    
    @Mock
    private GraphSeeker seeker;
    
    @Mock
    private GraphFilter filter;
    
    @Mock
    private MemoryOperations memoryAccess;
    
    @Mock
    private MemoryStats memoryStats;
    
    @Mock
    private DataMatchmaker dataMatchmaker;
    
    @Mock
    private GraphMappings mappings;
    
    @Mock
    private GraphDataSnapshotManager snapshotManager;
    
    @Before
    public void setUp() throws Exception {
        Constructor<?>[] constructors = GraphImpl.class.getDeclaredConstructors();
        constructors[0].setAccessible(true);
        graph = (GraphImpl) constructors[0].newInstance(Integer.valueOf(1));
        supervisor = new GraphSupervisorImpl(snapshotManager, memoryStats);
        
        ReflectionTestUtils.setField(graph, "writer", writer);
        ReflectionTestUtils.setField(graph, "reader", reader);
        ReflectionTestUtils.setField(graph, "filter", filter);
        ReflectionTestUtils.setField(graph, "seeker", seeker);
        ReflectionTestUtils.setField(graph, "mappings", mappings);
        ReflectionTestUtils.setField(graph, "memoryAccess", memoryAccess);
        ReflectionTestUtils.setField(graph, "supervisor", supervisor);
        ReflectionTestUtils.setField(graph, "dataMatchmaker", dataMatchmaker);
        
        graph.run();
    }

    @Test
    public void testGraphImpl() {
        assertNotNull(graph);
    }

    @Test
    public void testReadId() {
        when(reader.readId("some id")).thenReturn(gData);
        assertSame(gData, graph.readId("some id"));
        verify(reader).readId("some id");
    }

    @Test
    public void testReadSequence() {
        when(reader.readSequence(Sequence.valueOf(1))).thenReturn(gData);
        assertSame(gData, graph.readSequence(Sequence.valueOf(1)));
        verify(reader).readSequence(Sequence.valueOf(1));
    }

    @Test
    public void testReadReference() {
        when(reader.readReference(MemoryReference.valueOf(1))).thenReturn(gData);
        assertSame(gData, graph.readReference(MemoryReference.valueOf(1)));
        verify(reader).readReference(MemoryReference.valueOf(1));
    }

    @Test
    public void testReadIds() {
        String[] ids = new String[] {"id1", "id2", "id3"};
        GraphData[] values = new GraphData[] {gData, gData, gData};
        when(reader.readIds(ids)).thenReturn(values);
        assertSame(values, graph.readIds(ids));
        
        verify(reader).readIds(ids);
    }

    @Test
    public void testReadSequences() {
        Sequence[] sequences = new Sequence[] {Sequence.valueOf(1), Sequence.valueOf(2), Sequence.valueOf(3)};
        GraphData[] values = new GraphData[] {gData, gData, gData};
        when(reader.readSequences(sequences)).thenReturn(values);
        assertSame(values, graph.readSequences(sequences));
        
        verify(reader).readSequences(sequences);
    }

    @Test
    public void testReadReferences() {
        MemoryReference[] refs = new MemoryReference[] {MemoryReference.valueOf(1), MemoryReference.valueOf(2), MemoryReference.valueOf(3)};
        GraphData[] values = new GraphData[] {gData, gData, gData};
        when(reader.readReferences(refs)).thenReturn(values);
        assertSame(values, graph.readReferences(refs));
        
        verify(reader).readReferences(refs);
    }

    @Test
    public void testWriteData() throws GraphException {
        when(writer.write(data)).thenReturn(MemoryReference.valueOf(1));
        assertSame(MemoryReference.valueOf(1), graph.write(data));
        
        verify(writer).write(data);
    }

    @Test
    public void testWriteDataArray() throws GraphException {
        Data[] arrayOfData = new Data[] { data, data, data};
        MemoryReference[] values = new MemoryReference[] { 
                MemoryReference.valueOf(1), MemoryReference.valueOf(2), MemoryReference.valueOf(3)};
        when(writer.write(arrayOfData)).thenReturn(values);
        assertSame(values, graph.write(arrayOfData));
        
        verify(writer).write(arrayOfData);
    }

    @Test
    public void testDeleteGraphData() throws GraphException {
        graph.delete(gData);
        
        verify(writer).delete(gData);
    }

    @Test
    public void testDeleteGraphDataArray() throws GraphException {
        GraphData[] array = new GraphData[] { gData, gData, gData};
        
        graph.delete(array);
        
        verify(writer).delete(array);
    }

    @Test
    public void testDeleteString() throws GraphException {
        graph.delete("someId");
        verify(writer).delete("someId");
    }

    @Test
    public void testFilterBy() {
        GraphData[] values = new GraphData[] {gData, gData, gData};
        when(filter.filterBy(null)).thenReturn(values);
        
        assertSame(values, graph.filterBy(null));
        
        verify(filter).filterBy(null);
    }

    @Test
    public void testFilterByRangeMemoryReferenceMemoryReference() {
        GraphData[] values = new GraphData[] {gData, gData, gData};
        when(filter.filterByRange(MemoryReference.valueOf(1), MemoryReference.valueOf(2))).thenReturn(values);
        
        assertSame(values, graph.filterByRange(MemoryReference.valueOf(1), MemoryReference.valueOf(2)));
        
        verify(filter).filterByRange(MemoryReference.valueOf(1), MemoryReference.valueOf(2));
    }

    @Test
    public void testFilterByRangeSequenceSequence() {
        GraphData[] values = new GraphData[] {gData, gData, gData};
        when(filter.filterByRange(Sequence.valueOf(1), Sequence.valueOf(2))).thenReturn(values);
        
        assertSame(values, graph.filterByRange(Sequence.valueOf(1), Sequence.valueOf(2)));
        
        verify(filter).filterByRange(Sequence.valueOf(1), Sequence.valueOf(2));
    }

    @Test
    public void testClear() throws GraphException {
 
        graph.clear();
        
        verify(snapshotManager).clear();
    }

    @Test
    public void testIsEmpty() throws GraphException {
        assertTrue(graph.isEmpty());
        
        when(memoryStats.occupied()).thenReturn(1);
        
        assertFalse(graph.isEmpty());
        
        verify(memoryStats, times(2)).occupied();
    }

    @Test
    public void testCapacity() {
        when(memoryStats.capacity()).thenReturn(1);
        assertEquals(1, graph.capacity());
        
        verify(memoryStats).capacity();
    }

    @Test
    public void testOccupied() {
        when(memoryStats.occupied()).thenReturn(1);
        assertEquals(1, graph.occupied());
        
        verify(memoryStats).occupied();
    }

    @Test
    public void testAvailable() {
        when(memoryStats.available()).thenReturn(1);
        assertEquals(1, graph.available());
        
        verify(memoryStats).available();
    }

    @Test
    public void testRecycled() {
        when(memoryStats.recycled()).thenReturn(1);
        assertEquals(1, graph.recycled());
        
        verify(memoryStats).recycled();
    }

}
