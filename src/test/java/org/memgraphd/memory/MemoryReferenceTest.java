package org.memgraphd.memory;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class MemoryReferenceTest {
    private MemoryReference reference = MemoryReference.valueOf(1);
    
    @Test
    public void testHashCode() {
        assertEquals(1, reference.hashCode());
    }

    @Test
    public void testEqualsObject() {
        assertFalse(reference.equals(MemoryReference.valueOf(0)));
        assertTrue(reference.equals(MemoryReference.valueOf(1)));
    }

    @Test
    public void testId() {
        assertEquals(1, reference.id());
    }
    
    @Test(expected=RuntimeException.class)
    public void testId_negative() {
        MemoryReference.valueOf(-1);
    }
    
    @Test
    public void testValueOf() {
        assertSame(reference, MemoryReference.valueOf(1));
    }

    @Test
    public void testRangeOf_SameStartEnd() {
        MemoryReference refs1[] = MemoryReference.rangeOf(1, 1);
        assertEquals(1, refs1.length);
        assertEquals(1, refs1[0].id());
    }
    
    @Test(expected=RuntimeException.class)
    public void testRangeOf_StartNegative() {
        MemoryReference refs1[] = MemoryReference.rangeOf(-1, 1);
        assertEquals(1, refs1.length);
        assertEquals(1, refs1[0]);
    }
    
    @Test(expected=RuntimeException.class)
    public void testRangeOf_EndNegative() {
        MemoryReference refs1[] = MemoryReference.rangeOf(1, -1);
        assertEquals(1, refs1.length);
        assertEquals(1, refs1[0]);
    }
    
    @Test(expected=RuntimeException.class)
    public void testRangeOf_EndLessThanStart() {
        MemoryReference.rangeOf(1, 0);
    }
    
    @Test
    public void testRangeOf() {
        MemoryReference[] refs = MemoryReference.rangeOf(1, 10);
        assertEquals(10, refs.length);
        for(int i=1; i <= 10; i++) {
            assertEquals(i, refs[i - 1].id());
        }
    }
    
    @Test
    public void testOnStartup() {
        reference.onStartup();
    }

    @Test
    public void testOnShutdown() {
        reference.onShutdown();
        assertFalse(reference == MemoryReference.valueOf(1));
    }

    @Test
    public void testToString() {
        assertNotNull(reference.toString());
    }

}
