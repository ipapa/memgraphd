package org.memgraphd.security;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.memgraphd.GraphRequestType;
import org.memgraphd.data.GraphData;
import org.memgraphd.decision.DecisionMaker;
import org.memgraphd.decision.Sequence;
import org.memgraphd.exception.GraphException;
import org.memgraphd.operation.GraphReader;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LookupBySequenceValidatorTest {
    
    private LookupBySequenceValidator validator;
    
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
        validator = new LookupBySequenceValidator(decisionMaker, reader);
    }

    @Test
    public void testLookupBySequenceValidator() {
        assertNotNull(validator);
    }
    
    @Test
    public void testValidate_sequenceNull() throws GraphException {
        exception.expect(GraphException.class);
        exception.expectMessage("Decision is missing a sequence number.");
        
        validator.validate(null);
    }
    
    @Test
    public void testValidate_Succeeds() throws GraphException {
        Sequence seq = Sequence.valueOf(1L);
       
        when(decisionMaker.latestDecision()).thenReturn(seq);
        
        validator.validate(seq);
        
        verify(decisionMaker).latestDecision();
    }
    
    @Test
    public void testValidateGraphRequestTypeSequence_requestTypeNull() throws GraphException {
        Sequence seq = Sequence.valueOf(1L);
        exception.expect(GraphException.class);
        exception.expectMessage("Failed to resolve this request.");
        
        validator.validate(null, seq);
    }
    
    @Test
    public void testValidateGraphRequestTypeSequence_sequenceNull() throws GraphException {
        exception.expect(GraphException.class);
        exception.expectMessage("Decision is missing a sequence number.");
        
        validator.validate(GraphRequestType.UPDATE, null);
    }
    
    @Test
    public void testValidateGraphRequestTypeSequence_requestCREATE() throws GraphException {
        Sequence seq = Sequence.valueOf(1L);
        
        exception.expect(GraphException.class);
        exception.expectMessage("Data with sequence id=1 already exists in the graph.");
        when(decisionMaker.latestDecision()).thenReturn(seq);
        when(reader.readSequence(seq)).thenReturn(graphData);
        
        validator.validate(GraphRequestType.CREATE, seq);
        
        verify(decisionMaker).latestDecision();
        verify(reader).readSequence(seq);
    }
    
    @Test
    public void testValidateGraphRequestTypeSequence_requestCREATESucceeds() throws GraphException {
        Sequence seq = Sequence.valueOf(1L);
      
        when(decisionMaker.latestDecision()).thenReturn(seq);
        when(reader.readSequence(seq)).thenReturn(null);
        
        validator.validate(GraphRequestType.CREATE, seq);
        
        verify(decisionMaker).latestDecision();
        verify(reader).readSequence(seq);
    }
    
    @Test
    public void testValidateGraphRequestTypeSequence_requestRETRIEVE() throws GraphException {
        Sequence seq = Sequence.valueOf(1L);
        when(decisionMaker.latestDecision()).thenReturn(seq);
        
        validator.validate(GraphRequestType.RETRIEVE, seq);
        
        verify(decisionMaker).latestDecision();
        verifyZeroInteractions(reader);
    }
    
    @Test
    public void testValidateGraphRequestTypeSequence_requestUPDATE() throws GraphException {
        Sequence seq = Sequence.valueOf(1L);
        
        exception.expect(GraphException.class);
        exception.expectMessage("Data with sequence id=1 does not exist in the graph.");
        
        when(decisionMaker.latestDecision()).thenReturn(seq);
        when(reader.readSequence(seq)).thenReturn(null);
        
        validator.validate(GraphRequestType.UPDATE, seq);
        
        verify(decisionMaker).latestDecision();
        verify(reader).readSequence(seq);
    }
    
    @Test
    public void testValidateGraphRequestTypeSequence_requestUPDATESucceeds() throws GraphException {
        Sequence seq = Sequence.valueOf(1L);
       
        when(decisionMaker.latestDecision()).thenReturn(seq);
        when(reader.readSequence(seq)).thenReturn(graphData);
        
        validator.validate(GraphRequestType.UPDATE, seq);
        
        verify(decisionMaker).latestDecision();
        verify(reader).readSequence(seq);
    }
    
    @Test
    public void testValidateGraphRequestTypeSequence_requestDELETE() throws GraphException {
        Sequence seq = Sequence.valueOf(1L);
        
        exception.expect(GraphException.class);
        exception.expectMessage("Data with sequence id=1 does not exist in the graph.");
        
        when(decisionMaker.latestDecision()).thenReturn(seq);
        when(reader.readSequence(seq)).thenReturn(null);
        
        validator.validate(GraphRequestType.DELETE, seq);
        
        verify(decisionMaker).latestDecision();
        verify(reader).readSequence(seq);
    }
    
    @Test
    public void testValidateGraphRequestTypeSequence_requestDELETESucceeds() throws GraphException {
        Sequence seq = Sequence.valueOf(1L);
      
        when(decisionMaker.latestDecision()).thenReturn(seq);
        when(reader.readSequence(seq)).thenReturn(graphData);
        
        validator.validate(GraphRequestType.DELETE, seq);
        
        verify(decisionMaker).latestDecision();
        verify(reader).readSequence(seq);
    }

}
