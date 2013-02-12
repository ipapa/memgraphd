package org.memgraphd.security;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.memgraphd.GraphRequestType;
import org.memgraphd.data.Data;
import org.memgraphd.data.GraphData;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

@RunWith(MockitoJUnitRunner.class)
public class GraphRequestContextTest {
    
    private GraphRequestContext context;
    
    @Mock
    private Data data;
    
    @Mock
    private GraphData gData;
    
    @Before
    public void setUp() throws Exception {
        context = new GraphRequestContext(GraphRequestType.CREATE, data, gData);
    }

    @Test
    public void testGraphRequestContext() {
        assertNotNull(context);
    }

    @Test
    public void testGetRequestType() {
        assertSame(GraphRequestType.CREATE, context.getRequestType());
    }

    @Test
    public void testGetData() {
        assertSame(data, context.getData());
    }

    @Test
    public void testGetGraphData() {
        assertSame(gData, context.getGraphData());
    }

}
