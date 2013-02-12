package org.memgraphd.operation;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.memgraphd.GraphRequestType;
import org.memgraphd.data.Data;
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
    public void testWriteData_validationFailed() throws GraphException {
        when(resolver.resolve(GraphRequestType.CREATE, (Data)null)).thenReturn(context);
        doThrow(new GraphException("Data is null")).when(validator).validate(context);
        
        exception.expect(GraphException.class);
        exception.expectMessage("Data is null");
        
        writer.write((Data)null);
    }
    
    @Test
    public void testWriteData_authorizeCreate() throws GraphException {
        MemoryReference ref1 = MemoryReference.valueOf(1);
        
        when(resolver.resolve(GraphRequestType.CREATE, data)).thenReturn(context);
        when(decisionMaker.decidePutRequest(data)).thenReturn(decision);
        when(stateManager.create(decision)).thenReturn(ref1);
        
        assertEquals(ref1, writer.write(data));
        
        verify(resolver).resolve(GraphRequestType.CREATE, data);
        verify(validator).validate(context);
        verify(authority).authorize(context);
        verify(decisionMaker).decidePutRequest(data);
        verify(stateManager).create(decision);
    }
    
}
