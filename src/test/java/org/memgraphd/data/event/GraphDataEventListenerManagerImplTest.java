package org.memgraphd.data.event;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.memgraphd.data.GraphData;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GraphDataEventListenerManagerImplTest {
    
    private GraphDataEventListenerManager manager;
    
    private List<GraphDataEventListener> listeners;
    
    @Mock
    private GraphDataEventListener listener;
    
    @Mock
    private GraphDataEventHandler handler;
    
    @Mock
    private GraphData oldGraphData, graphData;
    
    @Before
    public void setUp() throws Exception {
        listeners = new ArrayList<GraphDataEventListener>();
        manager = new GraphDataEventListenerManagerImpl();
        ReflectionTestUtils.setField(manager, "listeners", listeners);
    }

    @Test
    public void testGraphDataEventListenerManagerImpl() {
        assertNotNull(manager);
    }

    @Test
    public void testAddEventListener() {
        assertEquals(0, listeners.size());
        manager.addEventListener(listener);
        assertEquals(1, listeners.size());
    }
    
    @Test
    public void testAddEventListener_twice() {
        assertEquals(0, listeners.size());
        manager.addEventListener(listener);
        manager.addEventListener(listener);
        assertEquals(1, listeners.size());
    }
    
    @Test
    public void testAddEventListener_null() {
        assertEquals(0, listeners.size());
        manager.addEventListener(null);
        assertEquals(0, listeners.size());
    }

    @Test
    public void testRemoveEventListener() {
        assertEquals(0, listeners.size());
        manager.addEventListener(listener);
        assertEquals(1, listeners.size());
        manager.removeEventListener(listener);
        assertEquals(0, listeners.size());
    }

    @Test
    public void testOnCreate() {
        when(listener.getHandler()).thenReturn(handler);
        manager.addEventListener(listener);
        manager.onCreate(graphData);
        verify(handler).onCreate(graphData);
    }

    @Test
    public void testOnUpdate() {
        when(listener.getHandler()).thenReturn(handler);
        manager.addEventListener(listener);
        manager.onUpdate(oldGraphData, graphData);
        verify(handler).onUpdate(oldGraphData, graphData);
    }

    @Test
    public void testOnDelete() {
        when(listener.getHandler()).thenReturn(handler);
        manager.addEventListener(listener);
        manager.onDelete(graphData);
        verify(handler).onDelete(graphData);
    }

}
