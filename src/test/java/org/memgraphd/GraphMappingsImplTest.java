package org.memgraphd;

import org.junit.Before;
import org.junit.Test;
import org.memgraphd.decision.Sequence;
import org.memgraphd.memory.MemoryReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class GraphMappingsImplTest {
    private GraphMappingsImpl mappings;
    
    @Before
    public void setUp() throws Exception {
        mappings = new GraphMappingsImpl();
    }

    @Test
    public void testGraphMappingsImpl() {
        assertNotNull(mappings);
    }

    @Test
    public void testContainsId() {
        assertFalse(mappings.containsId("some id"));
        mappings.put("some id", MemoryReference.valueOf(1));
        assertTrue(mappings.containsId("some id"));
    }

    @Test
    public void testGetById() {
        mappings.put("some id", MemoryReference.valueOf(1));
        assertSame(MemoryReference.valueOf(1), mappings.getById("some id"));
    }

    @Test
    public void testPutStringMemoryReference() {
        mappings.put("some id", MemoryReference.valueOf(1));
        assertSame(MemoryReference.valueOf(1), mappings.getById("some id"));
        mappings.put("some id", MemoryReference.valueOf(2));
        assertSame(MemoryReference.valueOf(2), mappings.getById("some id"));
    }

    @Test
    public void testContainsSequence() {
        assertFalse(mappings.containsSequence(Sequence.valueOf(1)));
        mappings.put(Sequence.valueOf(1), MemoryReference.valueOf(1));
        assertTrue(mappings.containsSequence(Sequence.valueOf(1)));
    }

    @Test
    public void testGetBySequence() {
        mappings.put(Sequence.valueOf(1), MemoryReference.valueOf(1));
        assertSame(MemoryReference.valueOf(1), mappings.getBySequence(Sequence.valueOf(1)));
    }

    @Test
    public void testPutSequenceMemoryReference() {
        mappings.put(Sequence.valueOf(1), MemoryReference.valueOf(1));
        assertSame(MemoryReference.valueOf(1), mappings.getBySequence(Sequence.valueOf(1)));
        
        mappings.put(Sequence.valueOf(1), MemoryReference.valueOf(2));
        assertSame(MemoryReference.valueOf(2), mappings.getBySequence(Sequence.valueOf(1)));
    }

    @Test
    public void testDeleteString() {
        mappings.delete("some id");
        mappings.put("some id", MemoryReference.valueOf(1));
        assertSame(MemoryReference.valueOf(1), mappings.getById("some id"));
        
        mappings.delete("some id");
        
        assertFalse(mappings.containsId("some id"));
        assertNull(mappings.getById("some id"));
    }

    @Test
    public void testDeleteSequence() {
        mappings.delete(Sequence.valueOf(1));
        mappings.put(Sequence.valueOf(1), MemoryReference.valueOf(1));
        assertSame(MemoryReference.valueOf(1), mappings.getBySequence(Sequence.valueOf(1)));
        
        mappings.delete(Sequence.valueOf(1));
        
        assertFalse(mappings.containsSequence(Sequence.valueOf(1)));
        assertNull(mappings.getBySequence(Sequence.valueOf(1)));
    }

    @Test
    public void testGetAllMemoryReferences() {
        assertTrue(mappings.getAllMemoryReferences().isEmpty());
        mappings.put("some id", MemoryReference.valueOf(1));
        assertEquals(1, mappings.getAllMemoryReferences().size());
        assertTrue(mappings.getAllMemoryReferences().contains(MemoryReference.valueOf(1)));
    }

}
