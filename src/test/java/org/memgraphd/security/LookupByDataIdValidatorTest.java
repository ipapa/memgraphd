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
import org.memgraphd.operation.GraphReader;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LookupByDataIdValidatorTest {
    
    private LookupByDataIdValidator validator;
    
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
        validator = new LookupByDataIdValidator(decisionMaker, reader);
    }

    @Test
    public void testLookupByDataIdValidator() {
        assertNotNull(validator);
    }

    @Test
    public void testValidateString_null() throws GraphException {
        exception.expect(GraphException.class);
        exception.expectMessage("Data id is invalid.");
        
        validator.validate(null);
    }
    
    @Test
    public void testValidateString_empty() throws GraphException {
        exception.expect(GraphException.class);
        exception.expectMessage("Data id is invalid.");
        
        validator.validate("");
    }
    
    @Test
    public void testValidateString_Succeeds() throws GraphException {
 
        validator.validate("id");
    }
    
    @Test
    public void testValidateGraphRequestTypeString_requestTypeNull() throws GraphException {
        exception.expect(GraphException.class);
        exception.expectMessage("Failed to resolve this request.");
        
        validator.validate(null, "id");
    }
    
    @Test
    public void testValidateGraphRequestTypeString_dataIdNull() throws GraphException {
        exception.expect(GraphException.class);
        exception.expectMessage("Data id is null or empty.");
        
        validator.validate(GraphRequestType.UPDATE, (String)null);
    }
    
    @Test
    public void testValidateGraphRequestTypeString_dataIdEmpty() throws GraphException {
        exception.expect(GraphException.class);
        exception.expectMessage("Data id is null or empty.");
        
        validator.validate(GraphRequestType.UPDATE, "");
    }
    
    @Test
    public void testValidateGraphRequestTypeString_requestCREATE() throws GraphException {
        exception.expect(GraphException.class);
        exception.expectMessage("Data id=id-1 already exists in the graph.");
        
        when(reader.readId("id-1")).thenReturn(graphData);
        
        validator.validate(GraphRequestType.CREATE, "id-1");
        
        verify(reader).readId("id-1");
    }
    
    @Test
    public void testValidateGraphRequestTypeString_requestCREATESucceeds() throws GraphException {
      
        when(reader.readId("id-1")).thenReturn(null);
        
        validator.validate(GraphRequestType.CREATE, "id-1");
        
        verify(reader).readId("id-1");
    }
    
    @Test
    public void testValidateGraphRequestTypeString_requestRETRIEVE() throws GraphException {
        
        validator.validate(GraphRequestType.RETRIEVE, "id-1");
        
        verifyZeroInteractions(reader);
    }
    
    @Test
    public void testValidateGraphRequestTypeString_requestUPDATE() throws GraphException {
        exception.expect(GraphException.class);
        exception.expectMessage("Data id=id-1 does not exist in the graph.");
        
        when(reader.readId("id-1")).thenReturn(null);
        
        validator.validate(GraphRequestType.UPDATE, "id-1");
        
        verify(reader).readId("id-1");
    }
    
    @Test
    public void testValidateGraphRequestTypeString_requestUPDATESucceeds() throws GraphException {
      
        when(reader.readId("id-1")).thenReturn(graphData);
        
        validator.validate(GraphRequestType.UPDATE, "id-1");
        
        verify(reader).readId("id-1");
    }
    
    @Test
    public void testValidateGraphRequestTypeString_requestDELETE() throws GraphException {
        exception.expect(GraphException.class);
        exception.expectMessage("Data id=id-1 does not exist in the graph.");
        
        when(reader.readId("id-1")).thenReturn(null);
        
        validator.validate(GraphRequestType.DELETE, "id-1");
        
        verify(reader).readId("id-1");
    }
    
    @Test
    public void testValidateGraphRequestTypeString_requestDELETESucceeds() throws GraphException {
    
        when(reader.readId("id-1")).thenReturn(graphData);
        
        validator.validate(GraphRequestType.DELETE, "id-1");
        
        verify(reader).readId("id-1");
    }
}
