package org.memgraphd.security;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.memgraphd.GraphRequestType;
import org.memgraphd.data.Data;
import org.memgraphd.decision.DecisionMaker;
import org.memgraphd.exception.GraphException;
import org.memgraphd.operation.GraphReader;
import org.memgraphd.test.data.TvEpisode;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InputDataValidatorTest {
    
    private InputDataValidator validator;
    
    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    @Mock
    private DecisionMaker decisionMaker;
    
    @Mock
    private GraphReader reader;
    
    @Mock
    private Data data;
    
    @Before
    public void setUp() throws Exception {
        validator = new InputDataValidator(decisionMaker, reader);
    }

    @Test
    public void testInputDataValidator() {
        assertNotNull(validator);
    }

    @Test
    public void testValidateData_null() throws GraphException {
        exception.expect(GraphException.class);
        exception.expectMessage("Data is null.");
        
        validator.validate((Data)null);
    }
    
    @Test
    public void testValidateData_dataIdNull() throws GraphException {
        exception.expect(GraphException.class);
        exception.expectMessage("Data id is invalid.");
        when(data.getId()).thenReturn(null);
        
        validator.validate(data);
    
        verify(data).getId();
    }
    
    @Test
    public void testValidateData_dataValidationFailed() throws GraphException {
        TvEpisode episode = new TvEpisode("id", null, null, null, null, null, null);
        exception.expect(GraphException.class);
        exception.expectMessage("Data validation failed.");
        
        validator.validate(episode);
    }
    
    @Test
    public void testValidateData_dataValidationSucceeds() throws GraphException {
        TvEpisode episode = new TvEpisode("id", null, null, "season-1", null, null, null);
      
        validator.validate(episode);
    }
    
    @Test
    public void testValidateData() throws GraphException {
        when(data.getId()).thenReturn("id");
        
        validator.validate(data);
    
        verify(data).getId();
    }
    
    @Test
    public void testValidateGraphRequestTypeData_requestTypeNull() throws GraphException {
        when(data.getId()).thenReturn("id");
        
        exception.expect(GraphException.class);
        exception.expectMessage("Failed to resolve this request.");
        
        validator.validate(null, data);
    
        verify(data).getId();
    }
    
    @Test
    public void testValidateGraphRequestTypeData_succeeds() throws GraphException {
        when(data.getId()).thenReturn("id");
        
        validator.validate(GraphRequestType.CREATE, data);
    
        verify(data).getId();
    }
}
