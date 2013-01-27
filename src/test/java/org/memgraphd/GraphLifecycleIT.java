package org.memgraphd;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.memgraphd.data.Data;
import org.memgraphd.data.ReadWriteData;
import org.memgraphd.data.GraphData;
import org.memgraphd.data.GraphDataImpl;
import org.memgraphd.decision.Sequence;
import org.memgraphd.exception.GraphException;
import org.memgraphd.memory.MemoryReference;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GraphLifecycleIT {

    private final int CAPACITY = 1000;
    
    private Data data;
    
    private Graph graph;
 
    @Before
    public void setUp() throws Exception {
        graph =  GraphImpl.build(new GraphConfigDefaults("someGraph", CAPACITY));
        data = new ReadWriteData(String.valueOf(1), new DateTime(), new DateTime());
    }
    
    @Test(expected=RuntimeException.class)
    public void testGraphNotRuning_ThrowsExceptionOnDelete() throws GraphException {
        graph.delete(data.getId());
    }
    
    @Test(expected=RuntimeException.class)
    public void testGraphNotRuning_ThrowsExceptionOnClear() throws GraphException {
        graph.clear();
    }
    
    @Test
    public void testGraphNotRuning_ThrowsExceptionOnIsEmpty() throws GraphException {
        assertTrue(graph.isEmpty());
    }
    
    @Test(expected=RuntimeException.class)
    public void testGraphNotRuning_ThrowsExceptionOnCapacity() throws GraphException {
        graph.capacity();
    }
    
    @Test(expected=RuntimeException.class)
    public void testGraphNotRuning_ThrowsExceptionOnOccupied() throws GraphException {
        graph.occupied();
    }
    
    @Test(expected=RuntimeException.class)
    public void testGraphNotRuning_ThrowsExceptionOnRecycled() throws GraphException {
        graph.recycled();
    }
    
    @Test(expected=RuntimeException.class)
    public void testGraphNotRuning_ThrowsExceptionOnAvailable() throws GraphException {
        graph.available();
    }
    
    @Test(expected=RuntimeException.class)
    public void testGraphNotRuning_ThrowsExceptionOnDeleteGraphData() throws GraphException {
        graph.delete(new GraphDataImpl(null));
    }
    
    @Test(expected=RuntimeException.class)
    public void testGraphNotRuning_ThrowsExceptionOnDeleteGraphDataArray() throws GraphException {
        graph.delete(new GraphData[] { new GraphDataImpl(null) });
    }
    
    @Test(expected=RuntimeException.class)
    public void testGraphNotRuning_ThrowsExceptionOnDeleteDataId() throws GraphException {
        graph.delete("id");
    }
    
    @Test(expected=RuntimeException.class)
    public void testGraphNotRuning_ThrowsExceptionOnFilterByMemoryBlock() throws GraphException {
        graph.filterBy(null);
    }
    
    @Test(expected=RuntimeException.class)
    public void testGraphNotRuning_ThrowsExceptionOnFilterByRangeOfMemoryReference() throws GraphException {
        graph.filterByRange(MemoryReference.valueOf(1), MemoryReference.valueOf(2));
    }
    
    @Test(expected=RuntimeException.class)
    public void testGraphNotRuning_ThrowsExceptionOnFilterByRangeOfSequences() throws GraphException {
        graph.filterByRange(Sequence.valueOf(1), Sequence.valueOf(2));
    }
    
    @Test(expected=RuntimeException.class)
    public void testGraphNotRuning_ThrowsExceptionOnReadId() throws GraphException {
        graph.readId("id");
    }
    
    @Test(expected=RuntimeException.class)
    public void testGraphNotRuning_ThrowsExceptionOnReadIds() throws GraphException {
        graph.readIds(new String[] { "id" });
    }
    
    @Test(expected=RuntimeException.class)
    public void testGraphNotRuning_ThrowsExceptionOnReadReference() throws GraphException {
        graph.readReference(MemoryReference.valueOf(1));
    }
    
    @Test(expected=RuntimeException.class)
    public void testGraphNotRuning_ThrowsExceptionOnReadReferences() throws GraphException {
        graph.readReferences(new MemoryReference[] { MemoryReference.valueOf(1)});
    }
    
    @Test(expected=RuntimeException.class)
    public void testGraphNotRuning_ThrowsExceptionOnReadSequence() throws GraphException {
        graph.readSequence(Sequence.valueOf(1));
    }
    
    @Test(expected=RuntimeException.class)
    public void testGraphNotRuning_ThrowsExceptionOnReadSequences() throws GraphException {
        graph.readSequences(new Sequence[] {Sequence.valueOf(1)});
    }
    
    @Test(expected=RuntimeException.class)
    public void testGraphNotRuning_ThrowsExceptionOnWrite() throws GraphException {
        graph.write(data);
    }
    
    @Test(expected=RuntimeException.class)
    public void testGraphNotRuning_ThrowsExceptionOnWrites() throws GraphException {
        graph.write(new Data[] { data });
    }
    
    @Test
    public void testRun() throws GraphException {
        graph.run();
        assertTrue(graph.isRunning());
    }
    
    @Test
    public void testShutdown() throws GraphException {
        graph.run();
        graph.shutdown();
        assertTrue(graph.isShutdown());
    }
    
    @Test
    public void testIsRunning() throws GraphException {
        assertFalse(graph.isRunning());
        graph.run();
        assertTrue(graph.isRunning());
        graph.shutdown();
        assertFalse(graph.isRunning());
    }
    
    @Test
    public void testisShutdown() throws GraphException {
        assertFalse(graph.isShutdown());
        graph.run();
        assertFalse(graph.isShutdown());
        graph.shutdown();
        assertTrue(graph.isShutdown());
    }
    
    @Test
    public void testIsInitialized() throws GraphException {
        assertTrue(graph.isInitialized());
        graph.run();
        assertTrue(graph.isInitialized());
        graph.shutdown();
        assertFalse(graph.isInitialized());
    }
    
    @Test
    public void testListenerNotifyOnEvent() throws GraphException {
        GraphLifecycleHandler handler = mock(GraphLifecycleHandler.class);
        graph.register(handler);
        
        graph.run();
        graph.shutdown();
        
        verify(handler).onStartup();
        verify(handler).onShutdown();
    }
    
    @Test
    public void testRemoveListener() throws GraphException {
        GraphLifecycleHandler handler = mock(GraphLifecycleHandler.class);
        graph.register(handler);
        
        graph.run();
        
        graph.unregister(handler);

        graph.shutdown();
        
        verify(handler).onStartup();
    }
    
    @Test(expected=RuntimeException.class)
    public void testshutdownpingWhenNotRunning() throws GraphException {
        graph.shutdown();
    }
    
    @Test(expected=RuntimeException.class)
    public void testGraphShutdown_ThrowsExceptionOnDelete() throws GraphException {
        graph.run();
        graph.shutdown();
        graph.delete(data.getId());
    }

    @Test(expected=RuntimeException.class)
    public void testGraphShutdown_ThrowsExceptionOnClear() throws GraphException {
        graph.run();
        graph.shutdown();
        graph.clear();
    }

    @Test(expected=RuntimeException.class)
    public void testGraphShutdown_ThrowsExceptionOnIsEmpty() throws GraphException {
        graph.run();
        graph.shutdown();
        graph.readId("someId");
    }

    @Test(expected=RuntimeException.class)
    public void testGraphShutdown_ThrowsExceptionOnCapacity() throws GraphException {
        graph.run();
        graph.shutdown();
        graph.capacity();
    }

    @Test(expected=RuntimeException.class)
    public void testGraphShutdown_ThrowsExceptionOnOccupied() throws GraphException {
        graph.run();
        graph.shutdown();
        graph.occupied();
    }

    @Test(expected=RuntimeException.class)
    public void testGraphShutdown_ThrowsExceptionOnRecycled() throws GraphException {
        graph.run();
        graph.shutdown();
        graph.recycled();
    }

    @Test(expected=RuntimeException.class)
    public void testGraphShutdown_ThrowsExceptionOnAvailable() throws GraphException {
        graph.run();
        graph.shutdown();
        graph.available();
    }

    @Test(expected=RuntimeException.class)
    public void testGraphShutdown_ThrowsExceptionOnDeleteGraphData() throws GraphException {
        graph.run();
        graph.shutdown();
        graph.delete(new GraphDataImpl(null));
    }

    @Test(expected=RuntimeException.class)
    public void testGraphShutdown_ThrowsExceptionOnDeleteGraphDataArray() throws GraphException {
        graph.run();
        graph.shutdown();
        graph.delete(new GraphData[] { new GraphDataImpl(null) });
    }

    @Test(expected=RuntimeException.class)
    public void testGraphShutdown_ThrowsExceptionOnDeleteDataId() throws GraphException {
        graph.run();
        graph.shutdown();
        graph.delete("id");
    }

    @Test(expected=RuntimeException.class)
    public void testGraphShutdown_ThrowsExceptionOnFilterByMemoryBlock() throws GraphException {
        graph.run();
        graph.shutdown();
        graph.filterBy(null);
    }

    @Test(expected=RuntimeException.class)
    public void testGraphShutdown_ThrowsExceptionOnFilterByRangeOfMemoryReference() throws GraphException {
        graph.run();
        graph.shutdown();
        graph.filterByRange(MemoryReference.valueOf(1), MemoryReference.valueOf(2));
    }

    @Test(expected=RuntimeException.class)
    public void testGraphShutdown_ThrowsExceptionOnFilterByRangeOfSequences() throws GraphException {
        graph.run();
        graph.shutdown();
        graph.filterByRange(Sequence.valueOf(1), Sequence.valueOf(2));
    }

    @Test(expected=RuntimeException.class)
    public void testGraphShutdown_ThrowsExceptionOnReadId() throws GraphException {
        graph.run();
        graph.shutdown();
        graph.readId("id");
    }

    @Test(expected=RuntimeException.class)
    public void testGraphShutdown_ThrowsExceptionOnReadIds() throws GraphException {
        graph.run();
        graph.shutdown();
        graph.readIds(new String[] { "id" });
    }

    @Test(expected=RuntimeException.class)
    public void testGraphShutdown_ThrowsExceptionOnReadReference() throws GraphException {
        graph.run();
        graph.shutdown();
        graph.readReference(MemoryReference.valueOf(1));
    }

    @Test(expected=RuntimeException.class)
    public void testGraphShutdown_ThrowsExceptionOnReadReferences() throws GraphException {
        graph.run();
        graph.shutdown();
        graph.readReferences(new MemoryReference[] { MemoryReference.valueOf(1)});
    }

    @Test(expected=RuntimeException.class)
    public void testGraphShutdown_ThrowsExceptionOnReadSequence() throws GraphException {
        graph.run();
        graph.shutdown();
        graph.readSequence(Sequence.valueOf(1));
    }

    @Test(expected=RuntimeException.class)
    public void testGraphShutdown_ThrowsExceptionOnReadSequences() throws GraphException {
        graph.run();
        graph.shutdown();
        graph.readSequences(new Sequence[] {Sequence.valueOf(1)});
    }

    @Test(expected=RuntimeException.class)
    public void testGraphShutdown_ThrowsExceptionOnWrite() throws GraphException {
        graph.run();
        graph.shutdown();
        graph.write(data);
    }

    @Test(expected=RuntimeException.class)
    public void testGraphShutdown_ThrowsExceptionOnWrites() throws GraphException {
        graph.run();
        graph.shutdown();
        graph.write(new Data[] { data });
    }
}
