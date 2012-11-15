package org.memgraphd;

import java.sql.SQLException;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.memgraphd.data.Data;
import org.memgraphd.data.DataImpl;
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
    public void setUp() throws SQLException {
        graph =  GraphImpl.build(CAPACITY);
        data = new DataImpl(String.valueOf(1), new DateTime(), new DateTime());
    }
    
    @Test(expected=RuntimeException.class)
    public void testGraphNotStarted_ThrowsExceptionOnDelete() throws GraphException {
        graph.delete(data.getId());
    }
    
    @Test(expected=RuntimeException.class)
    public void testGraphNotStarted_ThrowsExceptionOnClear() throws GraphException {
        graph.clear();
    }
    
    @Test
    public void testGraphNotStarted_ThrowsExceptionOnIsEmpty() throws GraphException {
        assertTrue(graph.isEmpty());
    }
    
    @Test(expected=RuntimeException.class)
    public void testGraphNotStarted_ThrowsExceptionOnCapacity() throws GraphException {
        graph.capacity();
    }
    
    @Test(expected=RuntimeException.class)
    public void testGraphNotStarted_ThrowsExceptionOnOccupied() throws GraphException {
        graph.occupied();
    }
    
    @Test(expected=RuntimeException.class)
    public void testGraphNotStarted_ThrowsExceptionOnRecycled() throws GraphException {
        graph.recycled();
    }
    
    @Test(expected=RuntimeException.class)
    public void testGraphNotStarted_ThrowsExceptionOnAvailable() throws GraphException {
        graph.available();
    }
    
    @Test(expected=RuntimeException.class)
    public void testGraphNotStarted_ThrowsExceptionOnDeleteGraphData() throws GraphException {
        graph.delete(new GraphDataImpl(null));
    }
    
    @Test(expected=RuntimeException.class)
    public void testGraphNotStarted_ThrowsExceptionOnDeleteGraphDataArray() throws GraphException {
        graph.delete(new GraphData[] { new GraphDataImpl(null) });
    }
    
    @Test(expected=RuntimeException.class)
    public void testGraphNotStarted_ThrowsExceptionOnDeleteDataId() throws GraphException {
        graph.delete("id");
    }
    
    @Test(expected=RuntimeException.class)
    public void testGraphNotStarted_ThrowsExceptionOnFilterByMemoryBlock() throws GraphException {
        graph.filterBy(null);
    }
    
    @Test(expected=RuntimeException.class)
    public void testGraphNotStarted_ThrowsExceptionOnFilterByRangeOfMemoryReference() throws GraphException {
        graph.filterByRange(MemoryReference.valueOf(1), MemoryReference.valueOf(2));
    }
    
    @Test(expected=RuntimeException.class)
    public void testGraphNotStarted_ThrowsExceptionOnFilterByRangeOfSequences() throws GraphException {
        graph.filterByRange(Sequence.valueOf(1), Sequence.valueOf(2));
    }
    
    @Test(expected=RuntimeException.class)
    public void testGraphNotStarted_ThrowsExceptionOnReadId() throws GraphException {
        graph.readId("id");
    }
    
    @Test(expected=RuntimeException.class)
    public void testGraphNotStarted_ThrowsExceptionOnReadIds() throws GraphException {
        graph.readIds(new String[] { "id" });
    }
    
    @Test(expected=RuntimeException.class)
    public void testGraphNotStarted_ThrowsExceptionOnReadReference() throws GraphException {
        graph.readReference(MemoryReference.valueOf(1));
    }
    
    @Test(expected=RuntimeException.class)
    public void testGraphNotStarted_ThrowsExceptionOnReadReferences() throws GraphException {
        graph.readReferences(new MemoryReference[] { MemoryReference.valueOf(1)});
    }
    
    @Test(expected=RuntimeException.class)
    public void testGraphNotStarted_ThrowsExceptionOnReadSequence() throws GraphException {
        graph.readSequence(Sequence.valueOf(1));
    }
    
    @Test(expected=RuntimeException.class)
    public void testGraphNotStarted_ThrowsExceptionOnReadSequences() throws GraphException {
        graph.readSequences(new Sequence[] {Sequence.valueOf(1)});
    }
    
    @Test(expected=RuntimeException.class)
    public void testGraphNotStarted_ThrowsExceptionOnWrite() throws GraphException {
        graph.write(data);
    }
    
    @Test(expected=RuntimeException.class)
    public void testGraphNotStarted_ThrowsExceptionOnWrites() throws GraphException {
        graph.write(new Data[] { data });
    }
    
    @Test
    public void testStart() {
        graph.start();
        assertTrue(graph.isRunning());
    }
    
    @Test
    public void testStop() {
        graph.start();
        graph.stop();
        assertTrue(graph.isStopped());
    }
    
    @Test
    public void testIsRunning() {
        assertFalse(graph.isRunning());
        graph.start();
        assertTrue(graph.isRunning());
        graph.stop();
        assertFalse(graph.isRunning());
    }
    
    @Test
    public void testIsStopped() {
        assertFalse(graph.isStopped());
        graph.start();
        assertFalse(graph.isStopped());
        graph.stop();
        assertTrue(graph.isStopped());
    }
    
    @Test
    public void testIsInitialized() {
        assertTrue(graph.isInitialized());
        graph.start();
        assertTrue(graph.isInitialized());
        graph.stop();
        assertFalse(graph.isInitialized());
    }
    
    @Test
    public void testListenerNotifyOnEvent() {
        GraphLifecycleHandler handler = mock(GraphLifecycleHandler.class);
        graph.register(handler);
        
        graph.start();
        graph.clear();
        graph.stop();
        
        verify(handler).onStartup();
        verify(handler).onClearAll();
        verify(handler).onShutdown();
    }
    
    @Test
    public void testRemoveListener() {
        GraphLifecycleHandler handler = mock(GraphLifecycleHandler.class);
        graph.register(handler);
        
        graph.start();
        
        graph.unregister(handler);
        
        graph.clear();
        graph.stop();
        
        verify(handler).onStartup();
    }
    
    @Test(expected=RuntimeException.class)
    public void testStoppingWhenNotRunning() {
        graph.stop();
    }
    
    @Test(expected=RuntimeException.class)
    public void testGraphStopped_ThrowsExceptionOnDelete() throws GraphException {
        graph.start();
        graph.stop();
        graph.delete(data.getId());
    }

    @Test(expected=RuntimeException.class)
    public void testGraphStopped_ThrowsExceptionOnClear() throws GraphException {
        graph.start();
        graph.stop();
        graph.clear();
    }

    @Test(expected=RuntimeException.class)
    public void testGraphStopped_ThrowsExceptionOnIsEmpty() throws GraphException {
        graph.start();
        graph.stop();
        graph.readId("someId");
    }

    @Test(expected=RuntimeException.class)
    public void testGraphStopped_ThrowsExceptionOnCapacity() throws GraphException {
        graph.start();
        graph.stop();
        graph.capacity();
    }

    @Test(expected=RuntimeException.class)
    public void testGraphStopped_ThrowsExceptionOnOccupied() throws GraphException {
        graph.start();
        graph.stop();
        graph.occupied();
    }

    @Test(expected=RuntimeException.class)
    public void testGraphStopped_ThrowsExceptionOnRecycled() throws GraphException {
        graph.start();
        graph.stop();
        graph.recycled();
    }

    @Test(expected=RuntimeException.class)
    public void testGraphStopped_ThrowsExceptionOnAvailable() throws GraphException {
        graph.start();
        graph.stop();
        graph.available();
    }

    @Test(expected=RuntimeException.class)
    public void testGraphStopped_ThrowsExceptionOnDeleteGraphData() throws GraphException {
        graph.start();
        graph.stop();
        graph.delete(new GraphDataImpl(null));
    }

    @Test(expected=RuntimeException.class)
    public void testGraphStopped_ThrowsExceptionOnDeleteGraphDataArray() throws GraphException {
        graph.start();
        graph.stop();
        graph.delete(new GraphData[] { new GraphDataImpl(null) });
    }

    @Test(expected=RuntimeException.class)
    public void testGraphStopped_ThrowsExceptionOnDeleteDataId() throws GraphException {
        graph.start();
        graph.stop();
        graph.delete("id");
    }

    @Test(expected=RuntimeException.class)
    public void testGraphStopped_ThrowsExceptionOnFilterByMemoryBlock() throws GraphException {
        graph.start();
        graph.stop();
        graph.filterBy(null);
    }

    @Test(expected=RuntimeException.class)
    public void testGraphStopped_ThrowsExceptionOnFilterByRangeOfMemoryReference() throws GraphException {
        graph.start();
        graph.stop();
        graph.filterByRange(MemoryReference.valueOf(1), MemoryReference.valueOf(2));
    }

    @Test(expected=RuntimeException.class)
    public void testGraphStopped_ThrowsExceptionOnFilterByRangeOfSequences() throws GraphException {
        graph.start();
        graph.stop();
        graph.filterByRange(Sequence.valueOf(1), Sequence.valueOf(2));
    }

    @Test(expected=RuntimeException.class)
    public void testGraphStopped_ThrowsExceptionOnReadId() throws GraphException {
        graph.start();
        graph.stop();
        graph.readId("id");
    }

    @Test(expected=RuntimeException.class)
    public void testGraphStopped_ThrowsExceptionOnReadIds() throws GraphException {
        graph.start();
        graph.stop();
        graph.readIds(new String[] { "id" });
    }

    @Test(expected=RuntimeException.class)
    public void testGraphStopped_ThrowsExceptionOnReadReference() throws GraphException {
        graph.start();
        graph.stop();
        graph.readReference(MemoryReference.valueOf(1));
    }

    @Test(expected=RuntimeException.class)
    public void testGraphStopped_ThrowsExceptionOnReadReferences() throws GraphException {
        graph.start();
        graph.stop();
        graph.readReferences(new MemoryReference[] { MemoryReference.valueOf(1)});
    }

    @Test(expected=RuntimeException.class)
    public void testGraphStopped_ThrowsExceptionOnReadSequence() throws GraphException {
        graph.start();
        graph.stop();
        graph.readSequence(Sequence.valueOf(1));
    }

    @Test(expected=RuntimeException.class)
    public void testGraphStopped_ThrowsExceptionOnReadSequences() throws GraphException {
        graph.start();
        graph.stop();
        graph.readSequences(new Sequence[] {Sequence.valueOf(1)});
    }

    @Test(expected=RuntimeException.class)
    public void testGraphStopped_ThrowsExceptionOnWrite() throws GraphException {
        graph.start();
        graph.stop();
        graph.write(data);
    }

    @Test(expected=RuntimeException.class)
    public void testGraphStopped_ThrowsExceptionOnWrites() throws GraphException {
        graph.start();
        graph.stop();
        graph.write(new Data[] { data });
    }
}
