package org.memgraphd.security;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.memgraphd.GraphRequestType;
import org.memgraphd.decision.DecisionMaker;
import org.memgraphd.decision.Sequence;
import org.memgraphd.exception.GraphException;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractInputValidatorTest {
    
    private AbstractInputValidator validator;
    
    @Mock
    private DecisionMaker decisionMaker;
    
    @Before
    public void setUp() throws Exception {
        validator = new GraphDataValidatorImpl(decisionMaker);
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

}
