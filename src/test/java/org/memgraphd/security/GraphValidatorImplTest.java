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
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertNotNull;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GraphValidatorImplTest {
    
    private GraphValidatorImpl validator;
    
    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    @Mock
    private DecisionMaker decisionMaker;

    @Mock
    private GraphRequestContext context;
    
    @Mock
    private DecisionValidator decisionValidator;
    
    @Mock
    private GraphDataValidator dataValidator;
    
    @Mock
    private Decision decision;
    
    @Mock
    private Data data;
    
    @Before
    public void setUp() throws Exception {
        validator = new GraphValidatorImpl(decisionMaker);

        ReflectionTestUtils.setField(validator, "decisionValidator", decisionValidator);
        ReflectionTestUtils.setField(validator, "dataValidator", dataValidator);
    }

    @Test
    public void testGraphValidatorImpl() {
        assertNotNull(validator);
    }
    
    @Test(expected=GraphException.class)
    public void testValidateDecision_throwsGraphException() throws GraphException {
        doThrow(new GraphException("")).when(decisionValidator).validate(decision);
        decisionValidator.validate(decision);
    }
    
    @Test
    public void testValidateDecision() throws GraphException {
        when(decision.getRequestType()).thenReturn(GraphRequestType.CREATE);
        when(decision.getData()).thenReturn(data);
        when(data.getId()).thenReturn("id");
        when(decision.getDataId()).thenReturn("id");
        when(decision.getSequence()).thenReturn(Sequence.valueOf(1L));
        when(decision.getTime()).thenReturn(DateTime.now());
        
        validator.validate(decision);
        verify(decisionValidator).validate(decision);
    }
    
    @Test(expected=GraphException.class)
    public void testValidateGraphRequestContext_throwsGraphException() throws GraphException {
        doThrow(new GraphException("")).when(dataValidator).validate(context);
        validator.validate(context);
    }
    
    @Test
    public void testValidateGraphRequestContext() throws GraphException {
        validator.validate(context);
        verify(dataValidator).validate(context);
    }
}
