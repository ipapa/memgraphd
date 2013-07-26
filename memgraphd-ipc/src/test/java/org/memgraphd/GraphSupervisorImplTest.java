package org.memgraphd;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.memgraphd.data.GraphDataSnapshotManager;
import org.memgraphd.exception.GraphException;
import org.memgraphd.memory.MemoryStats;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GraphSupervisorImplTest {
    private GraphSupervisor supervisor;
    
    @Mock
    private GraphLifecycleHandler handler;
    
    @Mock
    private GraphDataSnapshotManager snapshotManager;
    
    @Mock
    private MemoryStats memoryStats;
    
    @Before
    public void setUp() throws Exception {
        supervisor = new GraphSupervisorImpl(snapshotManager, memoryStats);
    }
    
    @Test
    public void testGraphSupervisorImpl() {
        assertNotNull(supervisor);
    }

    @Test
    public void testRegister() throws GraphException {
        supervisor.register(handler);
        supervisor.run();
        verify(handler).onStartup();
    }
    
    @Test
    public void testRegister_twice() throws GraphException {
        supervisor.register(handler);
        supervisor.register(handler);
        supervisor.run();
        verify(handler).onStartup();
    }

    @Test
    public void testUnregister() throws GraphException {
        // no exception unregistering something not registered
        supervisor.unregister(handler);
        
        supervisor.register(handler);
        supervisor.unregister(handler);
        
        supervisor.run();
        
        verifyZeroInteractions(handler);
    }
    
    @Test
    public void testStart() throws GraphException {
        supervisor.register(handler);
        supervisor.run();
        assertTrue(supervisor.isRunning());
        verify(handler).onStartup();
    }

    @Test
    public void testShutdown() throws GraphException {
        supervisor.register(handler);
        supervisor.run();
        supervisor.shutdown();
        assertTrue(supervisor.isShutdown());
        verify(handler).onShutdown();
    }
    
    @Test
    public void testClear() throws GraphException {
        supervisor.run();
        supervisor.clear();
        verify(snapshotManager).clear();
    }
    
    @Test
    public void testIsInitialized() throws GraphException {
        assertFalse(supervisor.isInitialized());
        supervisor.initialize();
        assertTrue(supervisor.isInitialized());
    }
    
    @Test
    public void testIsInitialized_whenRunning() throws GraphException {
        supervisor.run();
        assertTrue(supervisor.isInitialized());
    }

    @Test
    public void testIsRunning() throws GraphException {
        assertFalse(supervisor.isRunning());
        supervisor.run();
        assertTrue(supervisor.isRunning());
        supervisor.shutdown();
        assertFalse(supervisor.isRunning());
    }
    
    @Test
    public void testIsShutdown() throws GraphException {
        assertFalse(supervisor.isShutdown());
        supervisor.run();
        assertFalse(supervisor.isShutdown());
        supervisor.shutdown();
        assertTrue(supervisor.isShutdown());
    }
    
    @Test
    public void testIsEmpty() {
        when(memoryStats.occupied()).thenReturn(0);
        assertTrue(supervisor.isEmpty());
        when(memoryStats.occupied()).thenReturn(3);
        assertFalse(supervisor.isEmpty());
        
        verify(memoryStats, times(2)).occupied();
    }
    
    @Test
    public void testOccupied() {
        when(memoryStats.occupied()).thenReturn(9393);
        assertEquals(9393, supervisor.occupied());
       
        verify(memoryStats).occupied();
    }
    
    @Test
    public void testAvailable() {
        when(memoryStats.available()).thenReturn(9393);
        assertEquals(9393, supervisor.available());
       
        verify(memoryStats).available();
    }
    
    @Test
    public void testCapacity() {
        when(memoryStats.capacity()).thenReturn(9393);
        assertEquals(9393, supervisor.capacity());
       
        verify(memoryStats).capacity();
    }
    
    @Test
    public void testRecycled() {
        when(memoryStats.recycled()).thenReturn(9393);
        assertEquals(9393, supervisor.recycled());
       
        verify(memoryStats).recycled();
    }
}
