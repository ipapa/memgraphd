package org.memgraphd.data.relationship;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.memgraphd.data.Data;
import org.memgraphd.data.GraphData;
import org.memgraphd.memory.MemoryReference;
import org.memgraphd.memory.operation.MemoryOperations;
import org.memgraphd.operation.GraphSeeker;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DataMatchmakerImplTest {
    
    private DataMatchmakerImpl matchmaker;
    
    @Mock
    private MemoryOperations memoryAccess;
    
    @Mock
    private GraphSeeker seeker;
    
    @Mock
    private Data data;
    
    @Mock
    private GraphData gData;
    
    @Mock
    private DataRelationship dataRelationship, dataRelationship2;
    
    @Before
    public void setUp() throws Exception {
        matchmaker = new DataMatchmakerImpl(memoryAccess, seeker);
    }

    @Test
    public void testDataMatchmakerImpl() {
        assertNotNull(matchmaker);
    }

    @Test
    public void testBachelor_dataNull() {
        matchmaker.bachelor(null);
        verifyZeroInteractions(seeker);
    }
    
    @Test
    public void testBachelor_dataUnknown() {
        ReflectionTestUtils.setField(matchmaker, "singles", new HashMap<String, Set<MemoryReference>>());
        when(data.getId()).thenReturn("id");
        matchmaker.bachelor(data);
        verifyZeroInteractions(seeker);
    }
    
    @Test
    public void testBachelor_memoryReferenceNotFound() {
        Map<String, Set<MemoryReference>> map = new HashMap<String, Set<MemoryReference>>();
        map.put("id", null);
        ReflectionTestUtils.setField(matchmaker, "singles", map);
        when(data.getId()).thenReturn("id");
        when(seeker.seekById("id")).thenReturn(null);
        
        matchmaker.bachelor(data);
        
        verify(data, times(2)).getId();
        verify(seeker).seekById("id");
    }
    
    @Test
    public void testBachelor() {
        MemoryReference ref1 = MemoryReference.valueOf(1);
        MemoryReference ref2 = MemoryReference.valueOf(2);
        Map<String, Set<MemoryReference>> map = new HashMap<String, Set<MemoryReference>>();
        Set<MemoryReference> set = new HashSet<MemoryReference>();
        set.add(ref2);
        map.put("id", set);
        ReflectionTestUtils.setField(matchmaker, "singles", map);
        when(data.getId()).thenReturn("id");
        when(seeker.seekById("id")).thenReturn(ref1);

        matchmaker.bachelor(data);
        
        verify(data, times(2)).getId();
        verify(seeker).seekById("id");
        verify(memoryAccess).link(ref2, ref1);
    }

    @Test
    public void testMatch_dataNull() {
        matchmaker.match(null);
        verifyZeroInteractions(seeker);
    }
    
    @Test
    public void testMatch_IdNotFound() {
        when(dataRelationship.getId()).thenReturn("id");
        when(seeker.seekById("id")).thenReturn(null);
        
        matchmaker.match(dataRelationship);
        
        verify(seeker).seekById("id");
    }
    
    @Test
    public void testMatch() {
        MemoryReference ref1 = MemoryReference.valueOf(1);
        matchmaker = spy(matchmaker);
        when(dataRelationship.getId()).thenReturn("id");
        when(seeker.seekById("id")).thenReturn(ref1);
        
        matchmaker.match(dataRelationship);
        
        verify(matchmaker).vow(dataRelationship, ref1);
    }

    @Test
    public void testVow_dataNotFound() {
        MemoryReference ref1 = MemoryReference.valueOf(1);
        when(memoryAccess.read(ref1)).thenReturn(null);
        
        matchmaker.vow(dataRelationship, ref1);
        
        verifyZeroInteractions(seeker);
    }
    
    @Test
    public void testVow_dataFoundEmptyRelatedIds() {
        String[] emptyArray = new String[] {};
        MemoryReference[] emptyReferences = new MemoryReference[] {};
        MemoryReference ref1 = MemoryReference.valueOf(1);
        Map<String, Set<MemoryReference>> singles = new HashMap<String, Set<MemoryReference>>();
        
        ReflectionTestUtils.setField(matchmaker, "singles", singles);
        
        when(memoryAccess.read(ref1)).thenReturn(gData);
        when(dataRelationship.getRelatedIds()).thenReturn(emptyArray);
        when(seeker.seekById(emptyArray)).thenReturn(emptyReferences);
        
        matchmaker.vow(dataRelationship, ref1);
        assertTrue(singles.isEmpty());
        
        verify(seeker).seekById(emptyArray);
        verifyNoMoreInteractions(seeker);
    }
    
    @Test
    public void testVow_dataFoundTwoRelatedIds() {
        String[] ids = new String[] { "id1", "id2", "id3", "id4" };
        MemoryReference ref1 = MemoryReference.valueOf(1);
        MemoryReference ref2 = MemoryReference.valueOf(2);
        MemoryReference[] refs = new MemoryReference[] { ref1, ref2, null, null};
        
        Map<String, Set<MemoryReference>> singles = new HashMap<String, Set<MemoryReference>>();
        ReflectionTestUtils.setField(matchmaker, "singles", singles);
        
        when(memoryAccess.read(ref1)).thenReturn(gData);
        when(dataRelationship.getRelatedIds()).thenReturn(ids);
        when(seeker.seekById(ids)).thenReturn(refs);
        
        matchmaker.vow(dataRelationship, ref1);
        
        assertTrue(singles.containsKey("id3"));
        assertEquals(1, singles.get("id3").size());
        assertTrue(singles.get("id3").contains(ref1));
        assertTrue(singles.containsKey("id4"));
        assertEquals(1, singles.get("id4").size());
        assertTrue(singles.get("id4").contains(ref1));
        
        verify(seeker).seekById(ids);
        verify(memoryAccess).link(ref1, ref1);
        verify(memoryAccess).link(ref1, ref2);
    }

    @Test
    public void testRevow_oldVowsNull() {
        matchmaker.revow(null, dataRelationship2);
        verifyZeroInteractions(seeker);
        verifyZeroInteractions(memoryAccess);
    }
    
    @Test
    public void testRevow() {
        matchmaker = spy(matchmaker);
        MemoryReference ref1 = MemoryReference.valueOf(1);
        MemoryReference ref2 = MemoryReference.valueOf(2);
        MemoryReference ref3 = MemoryReference.valueOf(3);
        MemoryReference[] refs = new MemoryReference[] { ref2, ref3};
        String[] ids = new String[] { "id2", "id3" };
        when(dataRelationship.getId()).thenReturn("id");
        when(dataRelationship.getRelatedIds()).thenReturn(ids);
        when(seeker.seekById("id")).thenReturn(ref1);
        when(seeker.seekById(ids)).thenReturn(refs);
        
        matchmaker.revow(dataRelationship, dataRelationship2);
        
        verify(memoryAccess).delink(ref1, refs);
        verify(matchmaker).match(dataRelationship2);
    }

    @Test
    public void testSeparate_dataNull() {
        
        matchmaker.separate(null);
        verifyZeroInteractions(seeker);
        verifyZeroInteractions(memoryAccess);
    }
    
    @Test
    public void testSeparate_dataIdNotFound() {
        when(dataRelationship.getId()).thenReturn("id");
        when(seeker.seekById("id")).thenReturn(null);
        
        matchmaker.separate(dataRelationship);
        
        verifyZeroInteractions(memoryAccess);
    }
    
    @Test
    public void testSeparate() {
        MemoryReference ref1 = MemoryReference.valueOf(1);

        when(dataRelationship.getId()).thenReturn("id");
        when(seeker.seekById("id")).thenReturn(ref1);
        
        matchmaker.separate(dataRelationship);
        
        verify(memoryAccess).delinkAll(ref1);
        verify(memoryAccess).dereferenceAll(ref1);
    }

}
