package org.memgraphd.operation;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.memgraphd.GraphRequestType;
import org.memgraphd.data.Data;
import org.memgraphd.data.GraphData;
import org.memgraphd.decision.Decision;
import org.memgraphd.decision.DecisionMaker;
import org.memgraphd.decision.Sequence;
import org.memgraphd.exception.GraphException;
import org.memgraphd.memory.MemoryReference;
import org.memgraphd.test.data.TvEpisode;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

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
    private GraphData graphData;
    
    @Mock
    private Data data;
    
    @Mock
    private Decision decision;
    
    @Before
    public void setUp() throws Exception {
        validator = new GraphValidatorImpl(decisionMaker, reader);
    }

    @Test
    public void testGraphValidatorImpl() {
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
    public void testValidateDecision_null() throws GraphException {
        exception.expect(GraphException.class);
        exception.expectMessage("Decision is null.");
        
        validator.validate((Decision)null);
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
        when(decision.getRequestType()).thenReturn(GraphRequestType.RETRIEVE);
        
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
        
        validator.validate(GraphRequestType.UPDATE, (Sequence)null);
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

    @Test
    public void testValidateGraphRequestTypeMemoryReference_requestTypeNull() throws GraphException {
        MemoryReference ref = MemoryReference.valueOf(1);
        
        exception.expect(GraphException.class);
        exception.expectMessage("Failed to resolve this request.");
        
        validator.validate(null, ref);
    }
    
    @Test
    public void testValidateGraphRequestTypeMemoryReference_memoryReferenceNull() throws GraphException {
        exception.expect(GraphException.class);
        exception.expectMessage("Memory reference id is null.");
        
        validator.validate(GraphRequestType.UPDATE, (MemoryReference)null);
    }
    
    @Test
    public void testValidateGraphRequestTypeMemoryReference_memoryReferenceInvalid() throws GraphException {
        exception.expect(GraphException.class);
        exception.expectMessage("Memory reference id is invalid.");
        
        validator.validate(GraphRequestType.UPDATE, MemoryReference.valueOf(-100));
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
