package org.memgraphd.security;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.memgraphd.GraphRequestType;
import org.memgraphd.data.GraphData;
import org.memgraphd.decision.DecisionMaker;
import org.memgraphd.exception.GraphException;
import org.memgraphd.memory.MemoryReference;
import org.memgraphd.operation.GraphReader;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LookupByMemoryReferenceValidatorTest {
    
    private LookupByMemoryReferenceValidator validator;
    
    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    @Mock
    private DecisionMaker decisionMaker;
    
    @Mock
    private GraphReader reader;
    
    @Mock
    private GraphData graphData;
    
    @Before
    public void setUp() throws Exception {
        validator = new LookupByMemoryReferenceValidator(decisionMaker, reader);
    }

    @Test
    public void testLookupByMemoryReferenceValidator() {
        assertNotNull(validator);
    }
    
    @Test
    public void testValidateGraphRequestTypeMemoryReference_requestTypeNull() throws GraphException {
        MemoryReference ref = MemoryReference.valueOf(1);
        
        exception.expect(GraphException.class);
        exception.expectMessage("Failed to resolve this request.");
        
        validator.validate(null, ref);
    }
    
    @Test
    public void testValidate_memoryReferenceNull() throws GraphException {
        exception.expect(GraphException.class);
        exception.expectMessage("Memory reference id is null.");
        
        validator.validate(null);
    }
    
    @Test
    public void testValidate_memoryReferenceInvalid() throws GraphException {
        exception.expect(GraphException.class);
        exception.expectMessage("Memory reference id is invalid.");
        
        validator.validate(MemoryReference.valueOf(-100));
    }
    
    @Test
    public void testValidate_Succeeds() throws GraphException {
        
        validator.validate(MemoryReference.valueOf(1));
    }
    
    @Test
    public void testValidateGraphRequestTypeMemoryReference_requestCREATE() throws GraphException {
        MemoryReference ref = MemoryReference.valueOf(1);
        
        exception.expect(GraphException.class);
        exception.expectMessage("Memory reference id=1 already exists in the graph.");
       
        when(reader.readReference(ref)).thenReturn(graphData);
        
        validator.validate(GraphRequestType.CREATE, ref);

        verify(reader).readReference(ref);
    }
    
    @Test
    public void testValidateGraphRequestTypeMemoryReference_requestCREATESucceeds() throws GraphException {
        MemoryReference ref = MemoryReference.valueOf(1);
       
        when(reader.readReference(ref)).thenReturn(null);
        
        validator.validate(GraphRequestType.CREATE, ref);

        verify(reader).readReference(ref);
    }
    
    @Test
    public void testValidateGraphRequestTypeMemoryReference_requestRETRIEVE() throws GraphException {
        MemoryReference ref = MemoryReference.valueOf(1);
        
        validator.validate(GraphRequestType.RETRIEVE, ref);
        
        verifyZeroInteractions(reader);
    }
    
    @Test
    public void testValidateGraphRequestTypeMemoryReference_requestUPDATE() throws GraphException {
        MemoryReference ref = MemoryReference.valueOf(1);
        
        exception.expect(GraphException.class);
        exception.expectMessage("Memory reference id=1 does not exist in the graph.");
  
        when(reader.readReference(ref)).thenReturn(null);
        
        validator.validate(GraphRequestType.UPDATE, ref);

        verify(reader).readReference(ref);
    }
    
    @Test
    public void testValidateGraphRequestTypeMemoryReference_requestUPDATESucceeds() throws GraphException {
        MemoryReference ref = MemoryReference.valueOf(1);
    
        when(reader.readReference(ref)).thenReturn(graphData);
        
        validator.validate(GraphRequestType.UPDATE, ref);

        verify(reader).readReference(ref);
    }
    
    @Test
    public void testValidateGraphRequestTypeMemoryReference_requestDELETE() throws GraphException {
        MemoryReference ref = MemoryReference.valueOf(1);
        
        exception.expect(GraphException.class);
        exception.expectMessage("Memory reference id=1 does not exist in the graph.");
        
        when(reader.readReference(ref)).thenReturn(null);
        
        validator.validate(GraphRequestType.DELETE, ref);
  
        verify(reader).readReference(ref);
    }
    
    @Test
    public void testValidateGraphRequestTypeMemoryReference_requestDELETESucceeds() throws GraphException {
        MemoryReference ref = MemoryReference.valueOf(1);
        
        when(reader.readReference(ref)).thenReturn(graphData);
        
        validator.validate(GraphRequestType.DELETE, ref);
  
        verify(reader).readReference(ref);
    }

}
