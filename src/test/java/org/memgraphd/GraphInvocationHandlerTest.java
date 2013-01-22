package org.memgraphd;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.memgraphd.data.Data;
import org.memgraphd.data.GraphDataSnapshotManager;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertNotNull;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Method.class})
public class GraphInvocationHandlerTest {
    
    @Mock
    private Object proxy;
    
    @Mock
    private Graph graph;
    
    private GraphInvocationHandler handler;
    
    @Before
    public void setUp() throws Exception {
        handler = new GraphInvocationHandler(graph);
    }

    @Test
    public void testGraphInvocationHandler() {
        assertNotNull(handler);
    }
    
    @Test(expected=RuntimeException.class)
    public void testInvokeReadId_notRunning() throws Throwable {
        when(graph.isRunning()).thenReturn(false);
        handler.invoke(proxy, Graph.class.getMethod("readId", new Class<?>[] { String.class }), new Object[] { "id" });
        verify(graph).isRunning();
    }
    
    @Test
    public void testInvokeWriteData_Running() throws Throwable {
        when(graph.isRunning()).thenReturn(true);
        Data data = mock(Data.class);
        handler.invoke(proxy, Graph.class.getMethod("write", new Class<?>[] { Data.class }), new Object[] { data });
        verify(graph).isRunning();
    }
    
    @Test
    public void testInvokeReadId() throws Throwable {
        when(graph.isRunning()).thenReturn(true);
        handler.invoke(proxy, Graph.class.getMethod("readId", new Class<?>[] { String.class }), new Object[] { "id" });
        verify(graph).isRunning();
        verify(graph).readId("id");
    }
    
    @Test
    public void testInvokeWriteData() throws Throwable {
        when(graph.isRunning()).thenReturn(true);
        Data data = mock(Data.class);
        handler.invoke(proxy, Graph.class.getMethod("write", new Class<?>[] { Data.class }), new Object[] { data });
        verify(graph).isRunning();
        verify(graph).write(data);
    }
    
    @Test
    public void testInvoke_initialize() throws Throwable {
        handler.invoke(proxy, GraphDataSnapshotManager.class.getMethod("initialize", new Class<?>[] {}), null);
        verify(graph).initialize();
    }
    
    @Test(expected=RuntimeException.class)
    public void testInvoke_initialize_Running() throws Throwable {
        when(graph.isRunning()).thenReturn(true);
        handler.invoke(proxy, GraphDataSnapshotManager.class.getMethod("initialize", new Class<?>[] {}), null);
    }
    
    @Test(expected=RuntimeException.class)
    public void testInvoke_initialize_Stopped() throws Throwable {
        when(graph.isRunning()).thenReturn(false);
        when(graph.isShutdown()).thenReturn(true);
        handler.invoke(proxy, GraphDataSnapshotManager.class.getMethod("initialize", new Class<?>[] {}), null);
    }
    
    @Test
    public void testInvoke_clear() throws Throwable {
        when(graph.isShutdown()).thenReturn(false);
        when(graph.isRunning()).thenReturn(true);
        handler.invoke(proxy, GraphDataSnapshotManager.class.getMethod("clear", new Class<?>[] {}), null);
        verify(graph).isShutdown();
        verify(graph).isRunning();
        verify(graph).clear();
    }
    
    @Test(expected=RuntimeException.class)
    public void testInvoke_clear_notRunning() throws Throwable {
        when(graph.isShutdown()).thenReturn(false);
        when(graph.isRunning()).thenReturn(false);
        handler.invoke(proxy, GraphDataSnapshotManager.class.getMethod("clear", new Class<?>[] {}), null);
        verify(graph).isShutdown();
        verify(graph).isRunning();
        verify(graph).clear();
    }
    
    @Test(expected=RuntimeException.class)
    public void testInvoke_clear_Stopped() throws Throwable {
        when(graph.isShutdown()).thenReturn(true);
        handler.invoke(proxy, GraphDataSnapshotManager.class.getMethod("clear", new Class<?>[] {}), null);
        verify(graph).isShutdown();
        verify(graph).clear();
    }
 
    @Test
    public void testInvoke_run() throws Throwable {
        handler.invoke(proxy, GraphSupervisor.class.getMethod("run", new Class<?>[] {}), null);
        verify(graph).run();
    }
    
    @Test(expected=RuntimeException.class)
    public void testInvoke_run_AlreadyStarted() throws Throwable {
        when(graph.isRunning()).thenReturn(true);
        handler.invoke(proxy, GraphSupervisor.class.getMethod("run", new Class<?>[] {}), null);
        verify(graph).isRunning();
    }
    
    @Test
    public void testInvoke_shutdown() throws Throwable {
        when(graph.isShutdown()).thenReturn(false);
        when(graph.isRunning()).thenReturn(true);
        handler.invoke(proxy, GraphSupervisor.class.getMethod("shutdown", new Class<?>[] {}), null);
        verify(graph).isShutdown();
        verify(graph).isRunning();
        verify(graph).shutdown();
    }
    
    @Test(expected=RuntimeException.class)
    public void testInvoke_shutdown_AlreadyStopped() throws Throwable {
        when(graph.isShutdown()).thenReturn(true);
        handler.invoke(proxy, GraphSupervisor.class.getMethod("shutdown", new Class<?>[] {}), null);
    }
    
    @Test(expected=RuntimeException.class)
    public void testInvoke_shutdown_NotRunning() throws Throwable {
        when(graph.isShutdown()).thenReturn(false);
        when(graph.isRunning()).thenReturn(false);
        handler.invoke(proxy, GraphSupervisor.class.getMethod("shutdown", new Class<?>[] {}), null);
    }
    
    @Test
    public void testInvoke_isRunning() throws Throwable {
        handler.invoke(proxy, GraphSupervisor.class.getMethod("isRunning", new Class<?>[] {}), null);
        verify(graph).isRunning();
    }
    
    @Test
    public void testInvoke_isShutdown() throws Throwable {
        handler.invoke(proxy, GraphSupervisor.class.getMethod("isShutdown", new Class<?>[] {}), null);
        verify(graph).isShutdown();
    }
    
    @Test
    public void testInvoke_isInitialized() throws Throwable {
        handler.invoke(proxy, GraphSupervisor.class.getMethod("isInitialized", new Class<?>[] {}), null);
        verify(graph).isInitialized();
    }
    
    @Test
    public void testInvoke_register() throws Throwable {
        GraphLifecycleHandler arg = mock(GraphLifecycleHandler.class);
        handler.invoke(proxy, GraphSupervisor.class.getMethod("register", new Class<?>[] { GraphLifecycleHandler.class }), new Object[] {arg});
        verify(graph).register(arg);
    }
    
    @Test
    public void testInvoke_unregister() throws Throwable {
        GraphLifecycleHandler arg = mock(GraphLifecycleHandler.class);
        handler.invoke(proxy, GraphSupervisor.class.getMethod("unregister", new Class<?>[] { GraphLifecycleHandler.class }), new Object[] {arg});
        verify(graph).unregister(arg);
    }
    
}
