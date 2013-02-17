package org.memgraphd.operation;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.memgraphd.GraphRequestType;
import org.memgraphd.data.Data;
import org.memgraphd.data.GraphData;
import org.memgraphd.decision.Decision;
import org.memgraphd.decision.DecisionMaker;
import org.memgraphd.exception.GraphException;
import org.memgraphd.memory.MemoryReference;
import org.memgraphd.memory.operation.MemoryOperations;
import org.memgraphd.security.GraphAuthority;
import org.memgraphd.security.GraphRequestContext;
import org.memgraphd.security.GraphRequestResolver;
import org.memgraphd.security.GraphValidator;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GraphWriterImplTest {
    
    private GraphWriterImpl writer;
    
    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    @Mock
    private MemoryOperations memoryAccess;

    @Mock
    private DecisionMaker decisionMaker;
    
    @Mock
    private GraphRequestResolver resolver;
    
    @Mock
    private GraphValidator validator;
    
    @Mock
    private GraphAuthority authority;
    
    @Mock
    private GraphStateManager stateManager;
    
    @Mock
    private Data data;
    
    @Mock
    private GraphData graphData;
    
    @Mock
    private Decision decision;
    
    @Mock
    private GraphRequestContext context;
    
    @Before
    public void setUp() throws Exception {
        writer = new GraphWriterImpl(memoryAccess, authority, validator, resolver, decisionMaker, stateManager);
        ReflectionTestUtils.setField(writer, "authority", authority);
    }

    @Test
    public void testGraphWriterImpl() {
        assertNotNull(writer);
    }
    
    @Test
    public void testCreateData_validationFailed() throws GraphException {
        when(resolver.resolve(GraphRequestType.CREATE, (Data)null)).thenReturn(context);
        doThrow(new GraphException("Data is null")).when(validator).validate(context);
        
        exception.expect(GraphException.class);
        exception.expectMessage("Data is null");
        
        writer.create((Data)null);
    }
    
    @Test
    public void testCreateData_Success() throws GraphException {
        MemoryReference ref1 = MemoryReference.valueOf(1);
        
        when(resolver.resolve(GraphRequestType.CREATE, data)).thenReturn(context);
        when(decisionMaker.decide(context)).thenReturn(decision);
        when(stateManager.create(decision)).thenReturn(ref1);
        
        assertEquals(ref1, writer.create(data));
        
        verify(resolver).resolve(GraphRequestType.CREATE, data);
        verify(validator).validate(context);
        verify(authority).authorize(context);
        verify(decisionMaker).decide(context);
        verify(stateManager).create(decision);
    }
    
    @Test
    public void testUpdateData_validationFailed() throws GraphException {
        when(resolver.resolve(GraphRequestType.UPDATE, (Data)null)).thenReturn(context);
        doThrow(new GraphException("Data is null")).when(validator).validate(context);
        
        exception.expect(GraphException.class);
        exception.expectMessage("Data is null");
        
        writer.update((Data)null);
    }
    
    @Test
    public void testUpdateData_Success() throws GraphException {
        MemoryReference ref1 = MemoryReference.valueOf(1);
        
        when(resolver.resolve(GraphRequestType.UPDATE, data)).thenReturn(context);
        when(decisionMaker.decide(context)).thenReturn(decision);
        when(context.getGraphData()).thenReturn(graphData);
        when(stateManager.update(decision, graphData)).thenReturn(ref1);
        
        assertEquals(ref1, writer.update(data));
        
        verify(resolver).resolve(GraphRequestType.UPDATE, data);
        verify(validator).validate(context);
        verify(authority).authorize(context);
        verify(decisionMaker).decide(context);
        verify(stateManager).update(decision, graphData);
    }
    
    @Test
    public void testDeleteDataId_validationFailed() throws GraphException {
        when(resolver.resolve(GraphRequestType.DELETE, (String)null)).thenReturn(context);
        doThrow(new GraphException("Data is null")).when(validator).validate(context);
        
        exception.expect(GraphException.class);
        exception.expectMessage("Data is null");
        
        writer.delete((String)null);
    }
    
    @Test
    public void testDeleteDataId_Success() throws GraphException { 
        when(resolver.resolve(GraphRequestType.DELETE, "id")).thenReturn(context);
        when(decisionMaker.decide(context)).thenReturn(decision);
        when(context.getGraphData()).thenReturn(graphData);
        
        writer.delete("id");
        
        verify(resolver).resolve(GraphRequestType.DELETE, "id");
        verify(validator).validate(context);
        verify(authority).authorize(context);
        verify(decisionMaker).decide(context);
        verify(stateManager).delete(decision, graphData);
    }
    
    @Test
    public void testDeleteData_validationFailed() throws GraphException {
        when(resolver.resolve(GraphRequestType.DELETE, (Data)null)).thenReturn(context);
        doThrow(new GraphException("Data is null")).when(validator).validate(context);
        
        exception.expect(GraphException.class);
        exception.expectMessage("Data is null");
        
        writer.delete((Data)null);
    }
    
    @Test
    public void testDeleteData_Success() throws GraphException {
        MemoryReference ref1 = MemoryReference.valueOf(1);
        
        when(resolver.resolve(GraphRequestType.DELETE, data)).thenReturn(context);
        when(decisionMaker.decide(context)).thenReturn(decision);
        when(context.getGraphData()).thenReturn(graphData);
        when(stateManager.update(decision, graphData)).thenReturn(ref1);
        
        writer.delete(data);
        
        verify(resolver).resolve(GraphRequestType.DELETE, data);
        verify(validator).validate(context);
        verify(authority).authorize(context);
        verify(decisionMaker).decide(context);
        verify(stateManager).delete(decision, graphData);
    }
}
