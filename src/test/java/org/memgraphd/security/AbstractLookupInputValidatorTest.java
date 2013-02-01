package org.memgraphd.security;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.memgraphd.GraphRequestType;
import org.memgraphd.data.GraphData;
import org.memgraphd.decision.DecisionMaker;
import org.memgraphd.decision.Sequence;
import org.memgraphd.exception.GraphException;
import org.memgraphd.memory.MemoryReference;
import org.memgraphd.operation.GraphReader;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractLookupInputValidatorTest {
    
    private AbstractLookupInputValidator validator;
    
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
    public void testAbstractLookupInputValidator() {
        assertNotNull(validator);
    }

    @Test(expected=GraphException.class)
    public void testCheckRequestType() throws GraphException {
        validator.checkRequestType(null);
    }
    
    @Test
    public void testCheckRequestType_Succeeds() throws GraphException {
        validator.checkRequestType(GraphRequestType.CREATE);
    }

    @Test(expected=GraphException.class)
    public void testCheckDataIdNotNull_nullString() throws GraphException {
        validator.checkDataIdNotNull(null);
    }
    
    @Test(expected=GraphException.class)
    public void testCheckDataIdNotNull_empTyString() throws GraphException {
        validator.checkDataIdNotNull("");
    }
    
    @Test
    public void testCheckDataIdNotNull_Succeeds() throws GraphException {
        validator.checkDataIdNotNull("id");
    }

    @Test(expected=GraphException.class)
    public void testCheckDecisionSequence_null() throws GraphException {
        validator.checkDecisionSequence(null);
    }
    
    @Test(expected=GraphException.class)
    public void testCheckDecisionSequence_outOfRange() throws GraphException {
        Sequence seq1 = Sequence.valueOf(1);
        Sequence seq2 = Sequence.valueOf(300);
        when(decisionMaker.latestDecision()).thenReturn(seq1);
        
        validator.checkDecisionSequence(seq2);
    }
    
    @Test
    public void testCheckDecisionSequence_Succeeds() throws GraphException {
        Sequence seq1 = Sequence.valueOf(1);
        when(decisionMaker.latestDecision()).thenReturn(seq1);
        
        validator.checkDecisionSequence(seq1);
        
        verify(decisionMaker).latestDecision();
    }

    @Test
    public void testDataExistsString_true() {
        when(reader.readId("id")).thenReturn(graphData);
        
        assertTrue(validator.dataExists("id"));
        
        verify(reader).readId("id");
    }
    
    @Test
    public void testDataExistsString_false() {
        when(reader.readId("id")).thenReturn(null);
        
        assertFalse(validator.dataExists("id"));
        
        verify(reader).readId("id");
    }

    @Test
    public void testDataExistsMemoryReference_true() {
        MemoryReference ref = MemoryReference.valueOf(1);
        when(reader.readReference(ref)).thenReturn(graphData);
        
        assertTrue(validator.dataExists(ref));
        
        verify(reader).readReference(ref);
    }
    
    @Test
    public void testDataExistsMemoryReference_false() {
        MemoryReference ref = MemoryReference.valueOf(1);
        when(reader.readReference(ref)).thenReturn(null);
        
        assertFalse(validator.dataExists(ref));
        
        verify(reader).readReference(ref);
    }

    @Test
    public void testDataExistsSequence_true() {
        Sequence seq = Sequence.valueOf(1L);
        when(reader.readSequence(seq)).thenReturn(graphData);
        
        assertTrue(validator.dataExists(seq));
        
        verify(reader).readSequence(seq);
    }
    
    @Test
    public void testDataExistsSequence_false() {
        Sequence seq = Sequence.valueOf(1L);
        when(reader.readSequence(seq)).thenReturn(null);
        
        assertFalse(validator.dataExists(seq));
        
        verify(reader).readSequence(seq);
    }

}
