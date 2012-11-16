package org.memgraphd;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.memgraphd.data.Data;
import org.memgraphd.exception.GraphException;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.reflect.exceptions.MethodInvocationException;

import static org.junit.Assert.assertNotNull;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.spy;

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
    public void testInvoke_clear() throws Throwable {
        when(graph.isStopped()).thenReturn(false);
        when(graph.isRunning()).thenReturn(true);
        handler.invoke(proxy, GraphSupervisor.class.getMethod("clear", new Class<?>[] {}), null);
        verify(graph).isStopped();
        verify(graph).isRunning();
        verify(graph).clear();
    }
    
    @Test(expected=RuntimeException.class)
    public void testInvoke_clear_notRunning() throws Throwable {
        when(graph.isStopped()).thenReturn(false);
        when(graph.isRunning()).thenReturn(false);
        handler.invoke(proxy, GraphSupervisor.class.getMethod("clear", new Class<?>[] {}), null);
        verify(graph).isStopped();
        verify(graph).isRunning();
        verify(graph).clear();
    }
    
    @Test(expected=RuntimeException.class)
    public void testInvoke_clear_Stopped() throws Throwable {
        when(graph.isStopped()).thenReturn(true);
        handler.invoke(proxy, GraphSupervisor.class.getMethod("clear", new Class<?>[] {}), null);
        verify(graph).isStopped();
        verify(graph).clear();
    }
    
    @Test
    public void testInvoke_start() throws Throwable {
        handler.invoke(proxy, GraphSupervisor.class.getMethod("start", new Class<?>[] {}), null);
        verify(graph).start();
    }
    
    @Test(expected=RuntimeException.class)
    public void testInvoke_start_AlreadyStarted() throws Throwable {
        when(graph.isRunning()).thenReturn(true);
        handler.invoke(proxy, GraphSupervisor.class.getMethod("start", new Class<?>[] {}), null);
        verify(graph).isRunning();
    }
    
    @Test
    public void testInvoke_stop() throws Throwable {
        when(graph.isStopped()).thenReturn(false);
        when(graph.isRunning()).thenReturn(true);
        handler.invoke(proxy, GraphSupervisor.class.getMethod("stop", new Class<?>[] {}), null);
        verify(graph).isStopped();
        verify(graph).isRunning();
        verify(graph).stop();
    }
    
    @Test(expected=RuntimeException.class)
    public void testInvoke_stop_AlreadyStopped() throws Throwable {
        when(graph.isStopped()).thenReturn(true);
        handler.invoke(proxy, GraphSupervisor.class.getMethod("stop", new Class<?>[] {}), null);
    }
    
    @Test(expected=RuntimeException.class)
    public void testInvoke_stop_NotRunning() throws Throwable {
        when(graph.isStopped()).thenReturn(false);
        when(graph.isRunning()).thenReturn(false);
        handler.invoke(proxy, GraphSupervisor.class.getMethod("stop", new Class<?>[] {}), null);
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
    
    @Test(expected=Throwable.class)
    public void testInvoke_throwExceptionWithNoCause() throws Throwable {
        GraphInvocationHandler newHandler = spy(handler);
        Throwable exception = mock(Throwable.class);
        when(exception.getCause()).thenReturn(null);
        when(newHandler.invoke(proxy, GraphSupervisor.class.getMethod("isInitialized", new Class<?>[] {}), null)).thenThrow(exception);
        
        graph.isInitialized();
        
        verify(newHandler).invoke(proxy, GraphSupervisor.class.getMethod("isInitialized", new Class<?>[] {}), null);
    }
    
    @Test(expected=RuntimeException.class)
    public void testInvoke_throwExceptionWithCause() throws Throwable {
        when(graph.isInitialized()).thenThrow(new RuntimeException());
        
        graph.isInitialized();
        
        verify(graph).isInitialized();
    }

}
