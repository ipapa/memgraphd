package org.memgraphd.security;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.memgraphd.GraphRequestType;
import org.memgraphd.data.Data;
import org.memgraphd.data.GraphData;
import org.memgraphd.decision.Sequence;
import org.memgraphd.memory.MemoryReference;
import org.memgraphd.operation.GraphReader;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GraphRequestResolverImplTest {
    
    private GraphRequestResolverImpl resolver;
    
    @Mock
    private GraphReader reader;
    
    @Mock
    private GraphData gData;
    
    @Mock
    private Data data;
    
    @Before
    public void setUp() throws Exception {
        resolver = new GraphRequestResolverImpl(reader);
    }

    @Test
    public void testDefaultGraphRequestResolver() {
        assertNotNull(resolver);
    }

    @Test
    public void testResolveGraphRequestTypeString() {
        when(reader.readId("id")).thenReturn(gData);
        
        GraphRequestContext context = resolver.resolve(GraphRequestType.CREATE, "id");
        
        assertNotNull(context);
        assertSame(gData, context.getGraphData());
        assertSame(GraphRequestType.CREATE, context.getRequestType());
        assertNull(context.getData());
        
        verify(reader).readId("id");
    }
    
    @Test
    public void testResolveGraphRequestTypeString_null() {
       
        GraphRequestContext context = resolver.resolve(GraphRequestType.CREATE, (String)null);
        
        assertNotNull(context);
        assertNull(context.getGraphData());
        assertSame(GraphRequestType.CREATE, context.getRequestType());
        assertNull(context.getData());
    }
    
    @Test
    public void testResolveGraphRequestTypeString_emptyString() {
        
        GraphRequestContext context = resolver.resolve(GraphRequestType.CREATE, "");
        
        assertNotNull(context);
        assertNull(context.getGraphData());
        assertSame(GraphRequestType.CREATE, context.getRequestType());
        assertNull(context.getData());

    }
    
    @Test
    public void testResolveGraphRequestTypeData() {
        when(data.getId()).thenReturn("id");
        when(reader.readId("id")).thenReturn(gData);
        
        GraphRequestContext context = resolver.resolve(GraphRequestType.CREATE, data);
        
        assertNotNull(context);
        assertSame(gData, context.getGraphData());
        assertSame(GraphRequestType.CREATE, context.getRequestType());
        assertSame(data, context.getData());
        
        verify(data).getId();
        verify(reader).readId("id");
    }
    
    @Test
    public void testResolveGraphRequestTypeData_dataNull() {
 
        GraphRequestContext context = resolver.resolve(GraphRequestType.CREATE, (Data)null);
        
        assertNotNull(context);
        assertNull(context.getGraphData());
        assertSame(GraphRequestType.CREATE, context.getRequestType());
        assertNull(context.getData());
        
        verifyZeroInteractions(reader);
    }

    @Test
    public void testResolveGraphRequestTypeSequence() {
        Sequence seq = Sequence.valueOf(1);
        when(reader.readSequence(seq)).thenReturn(gData);
        
        GraphRequestContext context = resolver.resolve(GraphRequestType.CREATE, seq);
        
        assertNotNull(context);
        assertSame(gData, context.getGraphData());
        assertSame(GraphRequestType.CREATE, context.getRequestType());
        assertNull(context.getData());
        
        verify(reader).readSequence(seq);
    }
    
    @Test
    public void testResolveGraphRequestTypeSequence_sequenceNull() {

        GraphRequestContext context = resolver.resolve(GraphRequestType.CREATE, (Sequence)null);
        
        assertNotNull(context);
        assertNull(context.getGraphData());
        assertSame(GraphRequestType.CREATE, context.getRequestType());
        assertNull(context.getData());
        
        verifyZeroInteractions(reader);
    }

    @Test
    public void testResolveGraphRequestTypeMemoryReference() {
        MemoryReference ref = MemoryReference.valueOf(1);
        when(reader.readReference(ref)).thenReturn(gData);
        
        GraphRequestContext context = resolver.resolve(GraphRequestType.CREATE, ref);
        
        assertNotNull(context);
        assertSame(gData, context.getGraphData());
        assertSame(GraphRequestType.CREATE, context.getRequestType());
        assertNull(context.getData());
        
        verify(reader).readReference(ref);
    }
    
    @Test
    public void testResolveGraphRequestTypeMemoryReference_referenceNull() {

        GraphRequestContext context = resolver.resolve(GraphRequestType.CREATE, (MemoryReference)null);
        
        assertNotNull(context);
        assertNull(context.getGraphData());
        assertSame(GraphRequestType.CREATE, context.getRequestType());
        assertNull(context.getData());
        
        verifyZeroInteractions(reader);
    }
    
    @Test
    public void testResolveGraphRequestTypeGraphData_DELETE_Success() {

        when(reader.readId("id")).thenReturn(gData);
        when(gData.getData()).thenReturn(data);
        
        GraphRequestContext context = resolver.resolve(GraphRequestType.DELETE, "id");
        
        assertNotNull(context);
        assertSame(gData, context.getGraphData());
        assertSame(GraphRequestType.DELETE, context.getRequestType());
        assertSame(data, context.getData());

        verify(reader).readId("id");
        verify(gData).getData();
    }
    
    @Test
    public void testResolveGraphRequestTypeGraphData_DELETE_Failure() {

        when(reader.readId("id")).thenReturn(null);
        
        GraphRequestContext context = resolver.resolve(GraphRequestType.DELETE, "id");
        
        assertNotNull(context);
        assertNull(context.getGraphData());
        assertSame(GraphRequestType.DELETE, context.getRequestType());
        assertNull(context.getData());

        verify(reader).readId("id");
    }

}
