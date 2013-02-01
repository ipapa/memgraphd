package org.memgraphd.operation;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.memgraphd.GraphMappings;
import org.memgraphd.data.Data;
import org.memgraphd.data.GraphData;
import org.memgraphd.data.event.GraphDataEventListenerManager;
import org.memgraphd.data.library.Librarian;
import org.memgraphd.data.relationship.DataMatchmaker;
import org.memgraphd.decision.Decision;
import org.memgraphd.decision.DecisionMaker;
import org.memgraphd.decision.Sequence;
import org.memgraphd.exception.GraphException;
import org.memgraphd.memory.MemoryReference;
import org.memgraphd.memory.operation.MemoryOperations;
import org.memgraphd.security.GraphAuthority;
import org.memgraphd.test.data.TvEpisode;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import static org.mockito.Matchers.any;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GraphWriterImplTest {
    
    private GraphWriterImpl writer;
    
    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    @Mock
    private MemoryOperations memoryAccess;
    
    @Mock
    private GraphReader reader;
    
    @Mock
    private DecisionMaker decisionMaker;
    
    @Mock
    private GraphDataEventListenerManager eventManager;
    
    @Mock
    private GraphMappings mappings;
    
    @Mock
    private GraphSeeker seeker;
    
    @Mock
    private GraphAuthority authority;
    
    @Mock
    private DataMatchmaker matchmaker;
    
    @Mock
    private Librarian librarian;
    
    @Mock
    private GraphData graphData, graphData2;
    
    @Mock
    private Data data, data2;
    
    @Mock
    private Decision decision;
    
    @Before
    public void setUp() throws Exception {
        writer = new GraphWriterImpl(memoryAccess, reader, decisionMaker, eventManager, mappings, seeker, matchmaker, librarian);
        ReflectionTestUtils.setField(writer, "authority", authority);
    }

    @Test
    public void testGraphWriterImpl() {
        assertNotNull(writer);
    }
    
    @Test
    public void testWriteData_validationNull() throws GraphException {
        exception.expect(GraphException.class);
        exception.expectMessage("Data is null");
        
        writer.write((Data)null);
    }
    
    @Test
    public void testWriteData_validationFailed() throws GraphException {
        exception.expect(GraphException.class);
        exception.expectMessage("Data validation failed.");
        data = new TvEpisode(null, null, null, null, null, null, null);
        
        writer.write(data);
    }
    
    @Test
    public void testWriteData_validationSucceeds() throws GraphException {
        MemoryReference ref1 = MemoryReference.valueOf(1);
        Sequence seq1 = Sequence.valueOf(1L);
        
        data = new TvEpisode("id", null, null, "season-1", null, null, null);
        
        when(reader.readId("id")).thenReturn(null);
        when(decisionMaker.decidePutRequest(data)).thenReturn(decision);
        
        when(seeker.seekById("id")).thenReturn(ref1);
        when(memoryAccess.read(ref1)).thenReturn(graphData2);
        when(graphData2.getData()).thenReturn(data2);
        
        when(decision.getData()).thenReturn(data);
        when(decision.getSequence()).thenReturn(seq1);
        
        assertEquals(ref1, writer.write(data));
        
        verify(eventManager).onUpdate(any(GraphData.class), any(GraphData.class));
        verify(mappings).put(seq1, ref1);
        verify(librarian).archive(any(GraphData.class));
        verify(authority).grantInsert(data);
    }
    
    @Test
    public void testWriteData_authorizeInsert() throws GraphException {
        MemoryReference ref1 = MemoryReference.valueOf(1);
        Sequence seq1 = Sequence.valueOf(1L);
        
        when(data.getId()).thenReturn("id");
        when(data.canWrite()).thenReturn(true);
        when(reader.readId("id")).thenReturn(null);
        when(decisionMaker.decidePutRequest(data)).thenReturn(decision);
        
        when(seeker.seekById("id")).thenReturn(ref1);
        when(memoryAccess.read(ref1)).thenReturn(graphData2);
        when(graphData2.getData()).thenReturn(data2);
        
        when(decision.getData()).thenReturn(data);
        when(decision.getSequence()).thenReturn(seq1);
        
        assertEquals(ref1, writer.write(data));
        
        verify(eventManager).onUpdate(any(GraphData.class), any(GraphData.class));
        verify(mappings).put(seq1, ref1);
        verify(librarian).archive(any(GraphData.class));
        verify(authority).grantInsert(data);
    }
    
    @Test
    public void testWriteData_authorizeUpdate() throws GraphException {
        MemoryReference ref1 = MemoryReference.valueOf(1);
        Sequence seq1 = Sequence.valueOf(1L);
        
        when(data.getId()).thenReturn("id");
        when(data.canWrite()).thenReturn(true);
        when(reader.readId("id")).thenReturn(graphData2);
        when(decisionMaker.decidePutRequest(data)).thenReturn(decision);
        
        when(seeker.seekById("id")).thenReturn(ref1);
        when(memoryAccess.read(ref1)).thenReturn(graphData2);
        when(graphData2.getData()).thenReturn(data2);
        
        when(decision.getData()).thenReturn(data);
        when(decision.getSequence()).thenReturn(seq1);
        
        assertEquals(ref1, writer.write(data));
        
        verify(eventManager).onUpdate(any(GraphData.class), any(GraphData.class));
        verify(mappings).put(seq1, ref1);
        verify(librarian).archive(any(GraphData.class));
        verify(authority).grantUpdate(data2, data);
    }

    @Test
    public void testWriteDataArray() throws GraphException {
        MemoryReference ref1 = MemoryReference.valueOf(1);
        Sequence seq1 = Sequence.valueOf(1L);
        
        Data[] input = new Data[] { data, data2 };
        
        when(data.getId()).thenReturn("id");
        when(data.canWrite()).thenReturn(true);
        when(reader.readId("id")).thenReturn(graphData2);
        when(decisionMaker.decidePutRequest(data)).thenReturn(decision);
        when(decisionMaker.decidePutRequest(data2)).thenReturn(decision);
        
        when(seeker.seekById("id")).thenReturn(ref1);
        when(memoryAccess.read(ref1)).thenReturn(graphData2);
        when(graphData2.getData()).thenReturn(data2);
        
        when(decision.getData()).thenReturn(data);
        when(decision.getSequence()).thenReturn(seq1);
        
        MemoryReference[] refs = writer.write(input);
        assertNotNull(refs);
        assertEquals(2, input.length);
        
        verify(eventManager, times(2)).onUpdate(any(GraphData.class), any(GraphData.class));
        verify(mappings, times(2)).put(seq1, ref1);
        verify(librarian, times(2)).archive(any(GraphData.class));
        verify(authority).grantInsert(data2);
        verify(authority).grantUpdate(data2, data);
    }

    @Test
    public void testWriteDecision() {
        MemoryReference ref1 = MemoryReference.valueOf(1);
        Sequence seq1 = Sequence.valueOf(1L);
        
        when(data.getId()).thenReturn("id");
        when(data.canWrite()).thenReturn(true);
        when(reader.readId("id")).thenReturn(graphData2);
        when(decisionMaker.decidePutRequest(data)).thenReturn(decision);

        when(seeker.seekById("id")).thenReturn(ref1);
        when(memoryAccess.read(ref1)).thenReturn(graphData2);
        when(graphData2.getData()).thenReturn(data2);
        
        when(decision.getData()).thenReturn(data);
        when(decision.getSequence()).thenReturn(seq1);
        
        assertEquals(ref1, writer.write(decision));
        
        verify(eventManager).onUpdate(any(GraphData.class), any(GraphData.class));
        verify(mappings).put(seq1, ref1);
        verify(librarian).archive(any(GraphData.class));
    }

    @Test
    public void testDeleteGraphData_noRelationships() throws GraphException {
        MemoryReference ref1 = MemoryReference.valueOf(1);
        Sequence seq1 = Sequence.valueOf(1L);
        
        when(graphData.getData()).thenReturn(data);
        when(decisionMaker.decideDeleteRequest(data)).thenReturn(decision);
        when(decision.getData()).thenReturn(data);
        when(decision.getDataId()).thenReturn("id");
        when(graphData.getReference()).thenReturn(ref1);
        when(graphData.getSequence()).thenReturn(seq1);
        
        writer.delete(graphData);
        
        verify(authority).grantDelete(data);
        verify(memoryAccess).dereferenceAll(ref1);
        verify(memoryAccess).free(ref1);
        verify(mappings).delete("id");
        verify(mappings).delete(seq1);
        verify(librarian).unarchive(graphData);
        verify(eventManager).onDelete(graphData);
    }
    
    @Test
    public void testDeleteGraphData_withDataRelationships() throws GraphException {
        MemoryReference ref1 = MemoryReference.valueOf(1);
        Sequence seq1 = Sequence.valueOf(1L);
        
        TvEpisode episode = new TvEpisode("ep-1", null, null, null, null, null, null);
        when(graphData.getData()).thenReturn(data);
        when(decisionMaker.decideDeleteRequest(data)).thenReturn(decision);
        when(decision.getData()).thenReturn(episode);
        when(decision.getDataId()).thenReturn("ep-1");
        when(graphData.getReference()).thenReturn(ref1);
        when(graphData.getSequence()).thenReturn(seq1);
        
        writer.delete(graphData);
        
        verify(authority).grantDelete(data);
        verify(matchmaker).separate(episode);
        verify(memoryAccess).free(ref1);
        verify(mappings).delete("ep-1");
        verify(mappings).delete(seq1);
        verify(librarian).unarchive(graphData);
        verify(eventManager).onDelete(graphData);
    }
    
    @Test
    public void testDeleteGraphDataArray() throws GraphException {
        GraphData[] input = new GraphData[] { graphData };
        MemoryReference ref1 = MemoryReference.valueOf(1);
        Sequence seq1 = Sequence.valueOf(1L);
        
        when(graphData.getData()).thenReturn(data);
        when(decisionMaker.decideDeleteRequest(data)).thenReturn(decision);
        when(decision.getData()).thenReturn(data);
        when(decision.getDataId()).thenReturn("id");
        when(graphData.getReference()).thenReturn(ref1);
        when(graphData.getSequence()).thenReturn(seq1);
        
        writer.delete(input);
        
        verify(authority).grantDelete(data);
        verify(memoryAccess).dereferenceAll(ref1);
        verify(memoryAccess).free(ref1);
        verify(mappings).delete("id");
        verify(mappings).delete(seq1);
        verify(librarian).unarchive(graphData);
        verify(eventManager).onDelete(graphData);
    }

    @Test
    public void testDeleteString_doesNotExist() throws GraphException {
        when(reader.readId("2039")).thenReturn(null);
        exception.expect(GraphException.class);
        exception.expectMessage("Data does not exist for id=2039");
        
        writer.delete("2039");
        
        verify(reader).readId("2039");
        verifyZeroInteractions(memoryAccess);
        verifyZeroInteractions(mappings);
        verifyZeroInteractions(matchmaker);
        verifyZeroInteractions(librarian);
        verifyZeroInteractions(eventManager);
    }
    
    @Test
    public void testDeleteString_doesExist() throws GraphException {
        MemoryReference ref1 = MemoryReference.valueOf(1);
        Sequence seq1 = Sequence.valueOf(1L);
        
        when(reader.readId("id")).thenReturn(graphData);
        when(graphData.getData()).thenReturn(data);
        when(decisionMaker.decideDeleteRequest(data)).thenReturn(decision);
        when(decision.getData()).thenReturn(data);
        when(decision.getDataId()).thenReturn("id");
        when(graphData.getReference()).thenReturn(ref1);
        when(graphData.getSequence()).thenReturn(seq1);
        
        writer.delete("id");
        
        verify(reader).readId("id");
        verify(authority).grantDelete(data);
        verify(memoryAccess).dereferenceAll(ref1);
        verify(memoryAccess).free(ref1);
        verify(mappings).delete("id");
        verify(mappings).delete(seq1);
        verify(librarian).unarchive(graphData);
        verify(eventManager).onDelete(graphData);
        
    }

    @Test
    public void testDeleteDecision() throws GraphException {
        MemoryReference ref1 = MemoryReference.valueOf(1);
        Sequence seq1 = Sequence.valueOf(1L);
        
        when(reader.readId("id")).thenReturn(graphData);
        when(graphData.getData()).thenReturn(data);
        when(decisionMaker.decideDeleteRequest(data)).thenReturn(decision);
        when(decision.getData()).thenReturn(data);
        when(decision.getDataId()).thenReturn("id");
        when(graphData.getReference()).thenReturn(ref1);
        when(graphData.getSequence()).thenReturn(seq1);
        
        writer.delete(decision);
        
        verify(decision, times(3)).getDataId();
        verify(reader).readId("id");
        verify(authority).grantDelete(data);
        verify(memoryAccess).dereferenceAll(ref1);
        verify(memoryAccess).free(ref1);
        verify(mappings).delete("id");
        verify(mappings).delete(seq1);
        verify(librarian).unarchive(graphData);
        verify(eventManager).onDelete(graphData);
    }

}
