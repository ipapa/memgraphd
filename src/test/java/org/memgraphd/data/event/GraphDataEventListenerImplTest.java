package org.memgraphd.data.event;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

@RunWith(MockitoJUnitRunner.class)
public class GraphDataEventListenerImplTest {
    
    private GraphDataEventListener listener;
    
    @Mock
    private GraphDataEventHandler handler;
    
    @Before
    public void setUp() throws Exception {
        listener = new GraphDataEventListenerImpl(handler);
    }

    @Test
    public void testGraphDataEventListenerImpl() {
        assertNotNull(listener);
    }

    @Test
    public void testGetHandler() {
        assertSame(handler, listener.getHandler());
    }

}
