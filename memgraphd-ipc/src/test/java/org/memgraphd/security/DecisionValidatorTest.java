package org.memgraphd.security;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.memgraphd.GraphRequestType;
import org.memgraphd.data.Data;
import org.memgraphd.decision.Decision;
import org.memgraphd.decision.DecisionMaker;
import org.memgraphd.decision.Sequence;
import org.memgraphd.exception.GraphException;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DecisionValidatorTest {
    
    private DecisionValidator validator;
    
    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    @Mock
    private DecisionMaker decisionMaker;
    
    @Mock
    private Data data;
    
    @Mock
    private Decision decision;
    
    @Before
    public void setUp() throws Exception {
        validator = new DecisionValidator(decisionMaker);
    }

    @Test
    public void testDecisionValidator() {
        assertNotNull(validator);
    }
    
    @Test
    public void testValidateDecision_null() throws GraphException {
        exception.expect(GraphException.class);
        exception.expectMessage("Decision is null.");
        
        validator.validate(null);
    }
    
    @Test
    public void testValidateDecision_sequenceNull() throws GraphException {
        when(decision.getSequence()).thenReturn(null);
        
        exception.expect(GraphException.class);
        exception.expectMessage("Decision is missing a sequence number.");
        
        validator.validate(decision);
    
        verify(decision).getSequence();
    }
    
    @Test
    public void testValidateDecision_sequenceOutOfRange() throws GraphException {
        Sequence seq1 = Sequence.valueOf(1L);
        Sequence seq2 = Sequence.valueOf(0L);
        
        when(decision.getSequence()).thenReturn(seq1);
        when(decisionMaker.latestDecision()).thenReturn(seq2);
        
        exception.expect(GraphException.class);
        exception.expectMessage("Decision sequence number is out of range.");
        
        validator.validate(decision);
    
        verify(decision).getSequence();
        verify(decisionMaker).latestDecision();
    }
    
    @Test
    public void testValidateDecision_requestTypeNull() throws GraphException {
        Sequence seq1 = Sequence.valueOf(1L);
        when(decision.getSequence()).thenReturn(seq1);
        when(decisionMaker.latestDecision()).thenReturn(seq1);
        when(decision.getRequestType()).thenReturn(null);
        
        exception.expect(GraphException.class);
        exception.expectMessage("Decision is missing request type object.");
        
        validator.validate(decision);
    
        verify(decision).getSequence();
        verify(decisionMaker).latestDecision();
        verify(decision).getRequestType();
    }
    
    @Test
    public void testValidateDecision_requestTypeUnsupported() throws GraphException {
        Sequence seq1 = Sequence.valueOf(1L);
        when(decision.getSequence()).thenReturn(seq1);
        when(decisionMaker.latestDecision()).thenReturn(seq1);
        when(decision.getRequestType()).thenReturn(GraphRequestType.READ);
        
        exception.expect(GraphException.class);
        exception.expectMessage("Decision has an unsupported request type.");
        
        validator.validate(decision);
    
        verify(decision).getSequence();
        verify(decisionMaker).latestDecision();
        verify(decision).getRequestType();
    }
    
    @Test
    public void testValidateDecision_dataNull() throws GraphException {
        Sequence seq1 = Sequence.valueOf(1L);
        when(decision.getSequence()).thenReturn(seq1);
        when(decisionMaker.latestDecision()).thenReturn(seq1);
        when(decision.getRequestType()).thenReturn(GraphRequestType.CREATE);
        when(decision.getData()).thenReturn(null);
        
        exception.expect(GraphException.class);
        exception.expectMessage("Decision data is null.");
        
        validator.validate(decision);
    
        verify(decision).getSequence();
        verify(decisionMaker).latestDecision();
        verify(decision).getRequestType();
        verify(decision).getData();
    }
    
    @Test
    public void testValidateDecision_dataIdNull() throws GraphException {
        Sequence seq1 = Sequence.valueOf(1L);
        when(decision.getSequence()).thenReturn(seq1);
        when(decisionMaker.latestDecision()).thenReturn(seq1);
        when(decision.getRequestType()).thenReturn(GraphRequestType.CREATE);
        when(decision.getData()).thenReturn(data);
        when(data.getId()).thenReturn(null);
        
        exception.expect(GraphException.class);
        exception.expectMessage("Decision data id is null or empty.");
        
        validator.validate(decision);
    
        verify(decision).getSequence();
        verify(decisionMaker).latestDecision();
        verify(decision).getRequestType();
        verify(decision).getData();
        verify(data).getId();
    }
    
    @Test
    public void testValidateDecision_dataIdEmpty() throws GraphException {
        Sequence seq1 = Sequence.valueOf(1L);
        when(decision.getSequence()).thenReturn(seq1);
        when(decisionMaker.latestDecision()).thenReturn(seq1);
        when(decision.getRequestType()).thenReturn(GraphRequestType.CREATE);
        when(decision.getData()).thenReturn(data);
        when(data.getId()).thenReturn("");
        
        exception.expect(GraphException.class);
        exception.expectMessage("Decision data id is null or empty.");
        
        validator.validate(decision);
    
        verify(decision).getSequence();
        verify(decisionMaker).latestDecision();
        verify(decision).getRequestType();
        verify(decision).getData();
        verify(data).getId();
    }
    
    @Test
    public void testValidateDecision_decisionDataIdNull() throws GraphException {
        Sequence seq1 = Sequence.valueOf(1L);
        when(decision.getSequence()).thenReturn(seq1);
        when(decisionMaker.latestDecision()).thenReturn(seq1);
        when(decision.getRequestType()).thenReturn(GraphRequestType.CREATE);
        when(decision.getData()).thenReturn(data);
        when(decision.getDataId()).thenReturn(null);
        when(data.getId()).thenReturn("id");
        
        exception.expect(GraphException.class);
        exception.expectMessage("Decision data id is null or empty.");
        
        validator.validate(decision);
    
        verify(decision).getSequence();
        verify(decision).getDataId();
        verify(decisionMaker).latestDecision();
        verify(decision).getRequestType();
        verify(decision).getData();
        verify(data).getId();
    }
    
    @Test
    public void testValidateDecision_decisionDataIdEmpty() throws GraphException {
        Sequence seq1 = Sequence.valueOf(1L);
        when(decision.getSequence()).thenReturn(seq1);
        when(decisionMaker.latestDecision()).thenReturn(seq1);
        when(decision.getRequestType()).thenReturn(GraphRequestType.CREATE);
        when(decision.getData()).thenReturn(data);
        when(decision.getDataId()).thenReturn("");
        when(data.getId()).thenReturn("id");
        
        exception.expect(GraphException.class);
        exception.expectMessage("Decision data id is null or empty.");
        
        validator.validate(decision);
    
        verify(decision).getSequence();
        verify(decision).getDataId();
        verify(decisionMaker).latestDecision();
        verify(decision).getRequestType();
        verify(decision).getData();
        verify(data).getId();
    }
    
    @Test
    public void testValidateDecision_dataIdNotEqualWithDecisionDataId() throws GraphException {
        Sequence seq1 = Sequence.valueOf(1L);
        when(decision.getSequence()).thenReturn(seq1);
        when(decisionMaker.latestDecision()).thenReturn(seq1);
        when(decision.getRequestType()).thenReturn(GraphRequestType.CREATE);
        when(decision.getData()).thenReturn(data);
        when(data.getId()).thenReturn("id");
        when(decision.getDataId()).thenReturn("id2");
        
        exception.expect(GraphException.class);
        exception.expectMessage("Decision dataId does not match with data's id.");
        
        validator.validate(decision);
    
        verify(decision).getSequence();
        verify(decisionMaker).latestDecision();
        verify(decision).getRequestType();
        verify(decision).getData();
        verify(data).getId();
    }
    
    @Test
    public void testValidateDecision_decisionTimeNull() throws GraphException {
        Sequence seq1 = Sequence.valueOf(1L);
        when(decision.getSequence()).thenReturn(seq1);
        when(decisionMaker.latestDecision()).thenReturn(seq1);
        when(decision.getRequestType()).thenReturn(GraphRequestType.CREATE);
        when(decision.getData()).thenReturn(data);
        when(data.getId()).thenReturn("id");
        when(decision.getDataId()).thenReturn("id");
        when(decision.getTime()).thenReturn(null);
        
        exception.expect(GraphException.class);
        exception.expectMessage("Decision is missing the time decision was made.");
        
        validator.validate(decision);
    
        verify(decision).getSequence();
        verify(decisionMaker).latestDecision();
        verify(decision).getRequestType();
        verify(decision).getData();
        verify(decision).getTime();
        verify(data).getId();
    }
    
    @Test
    public void testValidateDecision_Succeeds() throws GraphException {
        Sequence seq1 = Sequence.valueOf(1L);
        when(decision.getSequence()).thenReturn(seq1);
        when(decisionMaker.latestDecision()).thenReturn(seq1);
        when(decision.getRequestType()).thenReturn(GraphRequestType.CREATE);
        when(decision.getData()).thenReturn(data);
        when(data.getId()).thenReturn("id");
        when(decision.getDataId()).thenReturn("id");
        when(decision.getTime()).thenReturn(DateTime.now());
        
        validator.validate(decision);
    
        verify(decision).getSequence();
        verify(decisionMaker).latestDecision();
        verify(decision).getRequestType();
        verify(decision).getData();
        verify(decision).getTime();
        verify(data, times(2)).getId();
    }
    
}
