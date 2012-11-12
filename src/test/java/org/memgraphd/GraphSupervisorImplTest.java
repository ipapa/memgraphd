package org.memgraphd;

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
    private GraphSupervisorImpl supervisor;
    
    @Mock
    private GraphLifecycleHandler handler;
    
    @Before
    public void setUp() {
        supervisor = new GraphImpl(1);
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

    @Test(expected=RuntimeException.class)
    public void testStart_alreadyRunning() {
        supervisor.start();
        supervisor.start();
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
    
    @Test(expected=RuntimeException.class)
    public void testStop_alreadyStopped() {
        supervisor.register(handler);
        supervisor.start();
        supervisor.stop();
        supervisor.stop();
        
        verify(handler).onStartup();
        verify(handler).onShutdown();
    }
    
    @Test(expected=RuntimeException.class)
    public void testStop_notRunning() {
        supervisor.stop();
    }
    
    @Test
    public void testIsStopped() {
        assertFalse(supervisor.isStopped());
        supervisor.start();
        assertFalse(supervisor.isStopped());
        supervisor.stop();
        assertTrue(supervisor.isStopped());
    }

    @Test(expected=RuntimeException.class)
    public void testAuthorize_initialized() {
        supervisor.authorize();
    }
    
    @Test(expected=RuntimeException.class)
    public void testAuthorize_stopped() {
        supervisor.start();
        supervisor.stop();
        supervisor.authorize();
    }
    
    @Test
    public void testAuthorize_running() {
        supervisor.start();
        supervisor.authorize();
    }

}
