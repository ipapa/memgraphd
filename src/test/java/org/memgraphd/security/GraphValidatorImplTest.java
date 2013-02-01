package org.memgraphd.security;

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
import org.memgraphd.memory.MemoryReference;
import org.memgraphd.operation.GraphReader;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertNotNull;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GraphValidatorImplTest {
    
    private GraphValidatorImpl validator;
    
    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    @Mock
    private DecisionMaker decisionMaker;
    
    @Mock
    private GraphReader reader;
    
    @Mock
    private Data data;
    
    @Mock
    private Decision decision;
    
    @Mock
    private GraphLookupInputValidator<String> dataIdValidator;
    
    @Mock
    private GraphLookupInputValidator<Sequence> sequenceValidator;
    
    @Mock
    private GraphLookupInputValidator<MemoryReference> memoryReferenceValidator;
    
    @Mock
    private GraphLookupInputValidator<Decision> decisionValidator;
    
    @Mock
    private GraphLookupInputValidator<Data> dataValidator;
    
    @Before
    public void setUp() throws Exception {
        validator = new GraphValidatorImpl(decisionMaker, reader);
        ReflectionTestUtils.setField(validator, "dataIdValidator", dataIdValidator);
        ReflectionTestUtils.setField(validator, "sequenceValidator", sequenceValidator);
        ReflectionTestUtils.setField(validator, "memoryReferenceValidator", memoryReferenceValidator);
        ReflectionTestUtils.setField(validator, "decisionValidator", decisionValidator);
        ReflectionTestUtils.setField(validator, "dataValidator", dataValidator);
    }

    @Test
    public void testGraphValidatorImpl() {
        assertNotNull(validator);
    }

    @Test(expected=GraphException.class)
    public void testValidateData_throwsException() throws GraphException {
        doThrow(new GraphException("")).when(dataValidator).validate(data);
        
        validator.validate(data);
        
        verify(dataValidator).validate(data);
    }
    
    @Test
    public void testValidateData_succeeds() throws GraphException {
        
        validator.validate(data);
        
        verify(dataValidator).validate(data);
    }
    
    @Test(expected=GraphException.class)
    public void testValidateDecision_throwsException() throws GraphException {
        doThrow(new GraphException("")).when(decisionValidator).validate(decision);
        
        validator.validate(decision);
        
        verify(decisionValidator).validate(decision);
    }
    
    @Test
    public void testValidateDecision_succeeds() throws GraphException {
        
        validator.validate(decision);
        
        verify(decisionValidator).validate(decision);
    }
    
    @Test(expected=GraphException.class)
    public void testValidateGraphRequestTypeString_throwsException() throws GraphException {
        doThrow(new GraphException("")).when(dataIdValidator).validate(GraphRequestType.CREATE, "id");
        
        validator.validate(GraphRequestType.CREATE, "id");
        
        verify(dataIdValidator).validate(GraphRequestType.CREATE, "id");
    }
    
    @Test
    public void testValidateGraphRequestTypeString_succeeds() throws GraphException {
        
        validator.validate(GraphRequestType.CREATE, "id");
        
        verify(dataIdValidator).validate(GraphRequestType.CREATE, "id");
    }
    
    @Test(expected=GraphException.class)
    public void testValidateGraphRequestTypeSequence_throwsException() throws GraphException {
        Sequence seq = Sequence.valueOf(1L);
        doThrow(new GraphException("")).when(sequenceValidator).validate(GraphRequestType.CREATE, seq);
        
        validator.validate(GraphRequestType.CREATE, seq);
        
        verify(sequenceValidator).validate(GraphRequestType.CREATE, seq);
    }
    
    @Test
    public void testValidateGraphRequestTypeSequence_succeeds() throws GraphException {
        Sequence seq = Sequence.valueOf(1L);
        validator.validate(GraphRequestType.CREATE, seq);
        
        verify(sequenceValidator).validate(GraphRequestType.CREATE, seq);
    }
    
    @Test(expected=GraphException.class)
    public void testValidateGraphRequestTypeMemoryReference_throwsException() throws GraphException {
        MemoryReference ref = MemoryReference.valueOf(1);
        doThrow(new GraphException("")).when(memoryReferenceValidator).validate(GraphRequestType.CREATE, ref);
        
        validator.validate(GraphRequestType.CREATE, ref);
        
        verify(memoryReferenceValidator).validate(GraphRequestType.CREATE, ref);
    }
    
    @Test
    public void testValidateGraphRequestTypeMemoryReference_succeeds() throws GraphException {
        MemoryReference ref = MemoryReference.valueOf(1);
        validator.validate(GraphRequestType.CREATE, ref);
        
        verify(memoryReferenceValidator).validate(GraphRequestType.CREATE, ref);
    }
}
