package org.memgraphd;

import java.lang.reflect.Constructor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.memgraphd.bookkeeper.BookKeeper;
import org.memgraphd.data.Data;
import org.memgraphd.data.GraphData;
import org.memgraphd.data.library.DefaultLibrary;
import org.memgraphd.data.relationship.DataMatchmaker;
import org.memgraphd.decision.DecisionMaker;
import org.memgraphd.decision.Sequence;
import org.memgraphd.exception.GraphException;
import org.memgraphd.memory.MemoryBlock;
import org.memgraphd.memory.MemoryBlockResolver;
import org.memgraphd.memory.MemoryReference;
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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GraphImplTest {
    
    private GraphImpl graph;
    
    @Mock
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
    private DataMatchmaker dataMatchmaker;
    
    @Mock
    private GraphMappings mappings;
    
    @Mock
    private GraphConfig config; 
    
    @Mock
    private MemoryBlockResolver memoryBlockResolver;
    
    @Mock
    private DecisionMaker decisionMaker;
    
    @Mock
    private BookKeeper bookKeeper;
    
    @Mock
    private DefaultLibrary librarian;
    
    @Before
    public void setUp() throws Exception {
        when(config.getName()).thenReturn("someName");
        when(config.getCapacity()).thenReturn(10);
        when(config.getBookKeeperDatabaseName()).thenReturn("dbName");
        when(config.getBookKeeperDatabasePath()).thenReturn("/tmp/book/");
        when(config.getMemoryBlockResolver()).thenReturn(memoryBlockResolver);
        when(config.getBookKeeper()).thenReturn(bookKeeper);
        when(config.getDecisionMaker()).thenReturn(decisionMaker);
        when(config.getBookKeeperOperationBatchSize()).thenReturn(1000L);
        when(config.getLibrarian()).thenReturn(librarian);
        
        when(memoryBlockResolver.blocks()).thenReturn(new MemoryBlock[] {});
        when(decisionMaker.latestDecision()).thenReturn(Sequence.valueOf(10));
        when(decisionMaker.getReadWriteBatchSize()).thenReturn(1000L);
        
        Constructor<?>[] constructors = GraphImpl.class.getDeclaredConstructors();
        constructors[0].setAccessible(true);
        graph = (GraphImpl) constructors[0].newInstance(config);
       
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
    
    @After
    public void tearDown() throws GraphException {
        graph.clear();
        graph.shutdown();
    }

    @Test
    public void testGraphImpl() {
        assertNotNull(graph);
    }
    
    @Test
    public void testGraphImpl_build() throws GraphException {
        assertNotNull(GraphImpl.build(config));
    }
    
    @Test
    public void testGetName() {
        assertEquals("someName", graph.getName());
    }
    
    @Test
    public void testReadId() {
        when(reader.read("some id")).thenReturn(gData);
        assertSame(gData, graph.read("some id"));
        verify(reader).read("some id");
    }

    @Test
    public void testReadSequence() {
        when(reader.read(Sequence.valueOf(1))).thenReturn(gData);
        assertSame(gData, graph.read(Sequence.valueOf(1)));
        verify(reader).read(Sequence.valueOf(1));
    }

    @Test
    public void testReadReference() {
        when(reader.read(MemoryReference.valueOf(1))).thenReturn(gData);
        assertSame(gData, graph.read(MemoryReference.valueOf(1)));
        verify(reader).read(MemoryReference.valueOf(1));
    }

    @Test
    public void testCreateData() throws GraphException {
        when(writer.create(data)).thenReturn(MemoryReference.valueOf(1));
        assertSame(MemoryReference.valueOf(1), graph.create(data));
        
        verify(writer).create(data);
    }
    
    @Test
    public void testUpdateData() throws GraphException {
        when(writer.update(data)).thenReturn(MemoryReference.valueOf(1));
        assertSame(MemoryReference.valueOf(1), graph.update(data));
        
        verify(writer).update(data);
    }


    @Test
    public void testDeleteString() throws GraphException {
        graph.delete("someId");
        verify(writer).delete("someId");
    }
    
    @Test
    public void testDeleteData() throws GraphException {
        graph.delete(data);
        verify(writer).delete(data);
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
    public void testRegister() throws GraphException {
        GraphLifecycleHandler handler = mock(GraphLifecycleHandler.class);
     
        graph.register(handler);
        
        verify(supervisor).register(handler);
    }
    
    @Test
    public void testUnregister() throws GraphException {
        GraphLifecycleHandler handler = mock(GraphLifecycleHandler.class);
     
        graph.unregister(handler);
        
        verify(supervisor).unregister(handler);
    }
    
    @Test
    public void testIsInitialized() throws GraphException {
        when(supervisor.isInitialized()).thenReturn(true);
        
        assertTrue(graph.isInitialized());
        
        verify(supervisor).isInitialized();
    }
    
    @Test
    public void testInitialize() throws GraphException {
      
        graph.initialize();
        
        verify(supervisor).initialize();
    }
    
    @Test
    public void testInitialize_bookClosed() throws GraphException {
        when(bookKeeper.isBookOpen()).thenReturn(false);
        
        graph.initialize();
        
        verify(bookKeeper, times(2)).isBookOpen();
        verify(bookKeeper, times(2)).openBook();
        verify(supervisor).initialize();
    }
    
    @Test
    public void testInitialize_bookOpen() throws GraphException {
        when(bookKeeper.isBookOpen()).thenReturn(true);
        
        graph.initialize();
        
        verify(bookKeeper, times(2)).isBookOpen();
        verify(supervisor).initialize();
    }
    
    @Test
    public void testRun() throws GraphException {
 
        graph.run();
        
        verify(supervisor, times(2)).run();
    }
    
    @Test
    public void testRun_bookClosed() throws GraphException {
        when(bookKeeper.isBookOpen()).thenReturn(false);
        
        graph.run();
        
        verify(bookKeeper, times(2)).isBookOpen();
        verify(bookKeeper, times(2)).openBook();
        verify(supervisor, times(2)).run();
    }
    
    @Test
    public void testRun_bookOpen() throws GraphException {
        when(bookKeeper.isBookOpen()).thenReturn(true);
        
        graph.run();
        
        verify(bookKeeper, times(2)).isBookOpen();
        verify(supervisor, times(2)).run();
    }
    
    @Test
    public void testShutdown() throws GraphException {
      
        graph.shutdown();
        
        verify(supervisor).shutdown();
    }
    
    @Test
    public void testShutdown_bookOpen() throws GraphException {
        when(bookKeeper.isBookOpen()).thenReturn(true);
        
        graph.shutdown();
        
        verify(bookKeeper, times(2)).isBookOpen();
        verify(bookKeeper).closeBook();
        verify(supervisor).shutdown();
    }
    
    @Test
    public void testClear() throws GraphException {
 
        graph.clear();
        
        verify(supervisor).clear();
    }

    @Test
    public void testIsEmpty() throws GraphException {
        when(supervisor.isEmpty()).thenReturn(true);
        
        assertTrue(graph.isEmpty());
        
        when(supervisor.isEmpty()).thenReturn(false);
        
        assertFalse(graph.isEmpty());
        
        verify(supervisor, times(2)).isEmpty();
    }

    @Test
    public void testCapacity() {
        when(supervisor.capacity()).thenReturn(1);
        assertEquals(1, graph.capacity());
        
        verify(supervisor).capacity();
    }

    @Test
    public void testOccupied() {
        when(supervisor.occupied()).thenReturn(1);
        assertEquals(1, graph.occupied());
        
        verify(supervisor).occupied();
    }

    @Test
    public void testAvailable() {
        when(supervisor.available()).thenReturn(1);
        assertEquals(1, graph.available());
        
        verify(supervisor).available();
    }

    @Test
    public void testRecycled() {
        when(supervisor.recycled()).thenReturn(1);
        assertEquals(1, graph.recycled());
        
        verify(supervisor).recycled();
    }
    
    @Test
    public void testGetLibrary() {
        assertSame(librarian, graph.getLibrary());
    }

}
