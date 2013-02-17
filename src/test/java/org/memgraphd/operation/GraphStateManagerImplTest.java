package org.memgraphd.operation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.memgraphd.GraphMappings;
import org.memgraphd.data.Data;
import org.memgraphd.data.GraphData;
import org.memgraphd.data.event.GraphDataEventListenerManager;
import org.memgraphd.data.library.Librarian;
import org.memgraphd.data.relationship.DataMatchmaker;
import org.memgraphd.decision.Decision;
import org.memgraphd.decision.Sequence;
import org.memgraphd.exception.GraphException;
import org.memgraphd.memory.MemoryReference;
import org.memgraphd.memory.operation.MemoryOperations;
import org.memgraphd.test.data.TvEpisode;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import static org.mockito.Matchers.any;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GraphStateManagerImplTest {
    
    private GraphStateManagerImpl stateManager;
    
    @Mock
    private MemoryOperations memoryAccess;
    
    @Mock
    private DataMatchmaker matchMaker;
    
    @Mock
    private GraphMappings mappings;
    
    @Mock
    private Librarian librarian;
    
    @Mock
    private GraphDataEventListenerManager eventManager;
    
    @Mock
    private Decision decision;
    
    @Mock
    private GraphData gData1;
    
    @Mock
    private Data data;
    
    @Before
    public void setUp() throws Exception {
        stateManager = new GraphStateManagerImpl(memoryAccess, mappings, librarian, matchMaker, eventManager);
    }

    @Test
    public void testGraphStateManagerImpl() {
        assertNotNull(stateManager);
    }

    @Test
    public void testCreate_bachelor() throws GraphException {
        MemoryReference ref = MemoryReference.valueOf(1);
        
        when(decision.getData()).thenReturn(data);
        when(memoryAccess.write(any(GraphData.class))).thenReturn(ref);
        when(data.getId()).thenReturn("id");
        
        assertEquals(ref, stateManager.create(decision));
        
        verify(memoryAccess).write(any(GraphData.class));
        verify(data, times(2)).getId();
        verify(mappings).put("id", ref);
        verify(eventManager).onCreate(any(GraphData.class));
        verify(matchMaker).bachelor(data);
    }
    
    @Test
    public void testCreate_match() throws GraphException {
        MemoryReference ref = MemoryReference.valueOf(1);
        TvEpisode episode = new TvEpisode("id", null, null, "season-1", null, "1", null);
        when(decision.getData()).thenReturn(episode);
        when(memoryAccess.write(any(GraphData.class))).thenReturn(ref);
        
        stateManager.create(decision);
        
        verify(memoryAccess).write(any(GraphData.class));
        verify(mappings).put("id", ref);
        verify(eventManager).onCreate(any(GraphData.class));
        verify(matchMaker).match(episode);
    }

    @Test
    public void testUpdate_bachelor() throws GraphException {
        MemoryReference ref = MemoryReference.valueOf(1);
        Sequence seq = Sequence.valueOf(1L);
        when(gData1.getReference()).thenReturn(ref);
        when(gData1.getReference()).thenReturn(ref);
        when(gData1.getData()).thenReturn(data);
        when(decision.getData()).thenReturn(data);
        when(decision.getSequence()).thenReturn(seq);
        
        assertEquals(ref, stateManager.update(decision, gData1));

        verify(matchMaker).bachelor(any(Data.class));
        verify(mappings).put(seq, ref);
        verify(librarian).archive(any(GraphData.class));
    }
    
    @Test
    public void testUpdate_revow() throws GraphException {
        MemoryReference ref = MemoryReference.valueOf(1);
        Sequence seq = Sequence.valueOf(1L);
        TvEpisode episode = new TvEpisode("id", null, null, "season-1", null, "1", null);
        
        when(gData1.getReference()).thenReturn(ref);
        when(gData1.getReference()).thenReturn(ref);
        when(gData1.getData()).thenReturn(episode);
        when(decision.getData()).thenReturn(episode);
        when(decision.getSequence()).thenReturn(seq);
        
        assertEquals(ref, stateManager.update(decision, gData1));

        verify(matchMaker).revow(episode, episode);
        verify(mappings).put(seq, ref);
        verify(librarian).archive(any(GraphData.class));
    }
    
    @Test
    public void testUpdate_separate() throws GraphException {
        MemoryReference ref = MemoryReference.valueOf(1);
        Sequence seq = Sequence.valueOf(1L);
        TvEpisode episode = new TvEpisode("id", null, null, "season-1", null, "1", null);
        
        when(gData1.getReference()).thenReturn(ref);
        when(gData1.getReference()).thenReturn(ref);
        when(gData1.getData()).thenReturn(episode);
        when(decision.getData()).thenReturn(data);
        when(decision.getSequence()).thenReturn(seq);
        
        assertEquals(ref, stateManager.update(decision, gData1));

        verify(matchMaker).separate(episode);
        verify(matchMaker).bachelor(any(Data.class));
        verify(mappings).put(seq, ref);
        verify(librarian).archive(any(GraphData.class));
    }
    
    @Test
    public void testUpdate_match() throws GraphException {
        MemoryReference ref = MemoryReference.valueOf(1);
        Sequence seq = Sequence.valueOf(1L);
        TvEpisode episode = new TvEpisode("id", null, null, "season-1", null, "1", null);
        
        when(gData1.getReference()).thenReturn(ref);
        when(gData1.getReference()).thenReturn(ref);
        when(gData1.getData()).thenReturn(data);
        when(decision.getData()).thenReturn(episode);
        when(decision.getSequence()).thenReturn(seq);
        
        assertEquals(ref, stateManager.update(decision, gData1));

        verify(matchMaker).match(episode);
        verify(mappings).put(seq, ref);
        verify(librarian).archive(any(GraphData.class));
    }
    
    @Test
    public void testDelete_single() throws GraphException {
        MemoryReference ref = MemoryReference.valueOf(1);
        Sequence seq = Sequence.valueOf(1L);
        
        when(decision.getData()).thenReturn(data);
        when(decision.getDataId()).thenReturn("id");
        when(gData1.getReference()).thenReturn(ref);
        when(gData1.getSequence()).thenReturn(seq);
        
        stateManager.delete(decision, gData1);
        
        verify(memoryAccess).free(ref);
        verify(memoryAccess).dereferenceAll(ref);
        verify(mappings).delete("id");
        verify(mappings).delete(seq);
        verify(eventManager).onDelete(gData1);
    }
    
    @Test
    public void testDelete_separate() throws GraphException {
        MemoryReference ref = MemoryReference.valueOf(1);
        Sequence seq = Sequence.valueOf(1L);
        TvEpisode episode = new TvEpisode("id", null, null, "season-1", null, "1", null);
        
        when(decision.getData()).thenReturn(episode);
        when(decision.getDataId()).thenReturn("id");
        when(gData1.getReference()).thenReturn(ref);
        when(gData1.getSequence()).thenReturn(seq);
        
        stateManager.delete(decision, gData1);
        
        verify(memoryAccess).free(ref);
        verify(matchMaker).separate(episode);
        verify(mappings).delete("id");
        verify(mappings).delete(seq);
        verify(eventManager).onDelete(gData1);
    }

}
