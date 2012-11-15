package org.memgraphd;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.memgraphd.data.Data;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
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
    
    @Test(expected=RuntimeException.class)
    public void testInvokeWriteData_notRunning() throws Throwable {
        when(graph.isRunning()).thenReturn(false);
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
    public void testInvoke_start() throws Throwable {
        handler.invoke(proxy, GraphSupervisor.class.getMethod("start", new Class<?>[] {}), null);
        verify(graph).start();
    }
    
    @Test
    public void testInvoke_stop() throws Throwable {
        handler.invoke(proxy, GraphSupervisor.class.getMethod("stop", new Class<?>[] {}), null);
        verify(graph).stop();
    }
    
    @Test
    public void testInvoke_isRunning() throws Throwable {
        handler.invoke(proxy, GraphSupervisor.class.getMethod("isRunning", new Class<?>[] {}), null);
        verify(graph).isRunning();
    }
    
    @Test
    public void testInvoke_isStopped() throws Throwable {
        handler.invoke(proxy, GraphSupervisor.class.getMethod("isStopped", new Class<?>[] {}), null);
        verify(graph).isStopped();
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
