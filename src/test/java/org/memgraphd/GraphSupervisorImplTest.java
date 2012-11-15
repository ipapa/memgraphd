package org.memgraphd;

import java.lang.reflect.Constructor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class GraphSupervisorImplTest {
    private GraphSupervisor supervisor;
    
    @Mock
    private GraphLifecycleHandler handler;
    
    @Before
    public void setUp() throws Exception {
        Constructor<?>[] constructors = GraphImpl.class.getDeclaredConstructors();
        constructors[0].setAccessible(true);
        supervisor = (GraphImpl) constructors[0].newInstance(Integer.valueOf(1));
    }
    
    @Test
    public void testGraphSupervisorImpl() {
        assertNotNull(supervisor);
    }

    @Test
    public void testRegister() {
        supervisor.register(handler);
        supervisor.start();
        verify(handler).onStartup();
    }

    @Test
    public void testUnregister() {
        // no exception unregistering something not registered
        supervisor.unregister(handler);
        
        supervisor.register(handler);
        supervisor.unregister(handler);
        
        supervisor.start();
        
        verifyZeroInteractions(handler);
    }
    
    @Test
    public void testStart() {
        supervisor.register(handler);
        supervisor.start();
        assertTrue(supervisor.isRunning());
        verify(handler).onStartup();
    }

    @Test
    public void testStop() {
        supervisor.register(handler);
        supervisor.start();
        supervisor.stop();
        assertTrue(supervisor.isStopped());
        verify(handler).onShutdown();
    }

    @Test
    public void testIsInitialized() {
        assertTrue(supervisor.isInitialized());
    }

    @Test
    public void testIsRunning() {
        assertFalse(supervisor.isRunning());
        supervisor.start();
        assertTrue(supervisor.isRunning());
        supervisor.stop();
        assertFalse(supervisor.isRunning());
    }
    
    @Test
    public void testIsStopped() {
        assertFalse(supervisor.isStopped());
        supervisor.start();
        assertFalse(supervisor.isStopped());
        supervisor.stop();
        assertTrue(supervisor.isStopped());
    }

}
