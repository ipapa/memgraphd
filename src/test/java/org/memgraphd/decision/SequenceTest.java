package org.memgraphd.decision;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class SequenceTest {
    private Sequence sequence = Sequence.valueOf(1);
    
    @Test
    public void testHashCode() {
        assertEquals(1, sequence.hashCode());
    }

    @Test
    public void testNumber() {
        assertEquals(1, sequence.number());
    }
    
    @Test
    public void testEqualsObject() {
        assertTrue(sequence.equals(Sequence.valueOf(1)));
        assertFalse(sequence.equals(Sequence.valueOf(2)));
    }

    @Test
    public void testRangeOf() {
        Sequence[] seqs = Sequence.rangeOf(1, 10);
        assertEquals(10, seqs.length);
        for(int i=1; i < 10; i++) {
            assertEquals(i, seqs[i - 1].number());
        }
    }
    
    @Test
    public void testRangeOf_startSameAsEnd() {
        Sequence[] seqs = Sequence.rangeOf(1, 1);
        assertEquals(1, seqs.length);
        assertEquals(1, seqs[0].number());
    }
    
    @Test(expected=RuntimeException.class)
    public void testRangeOf_startNegative() {
        Sequence.rangeOf(-1, 1);
    }
    
    @Test(expected=RuntimeException.class)
    public void testRangeOf_endNegative() {
        Sequence.rangeOf(1, -1);
    }

    @Test
    public void testValueOf() {
        assertSame(sequence, Sequence.valueOf(1));
    }

    @Test(expected=RuntimeException.class)
    public void testValueOf_negative() {
        Sequence.valueOf(-1);
    }

    @Test
    public void testOnStartup() {
        sequence.onStartup();
        assertSame(sequence, Sequence.valueOf(1));
    }

    @Test
    public void testOnClearAll() {
        sequence.onClearAll();
        assertSame(sequence, Sequence.valueOf(1));
    }

    @Test
    public void testOnShutdown() {
        sequence.onShutdown();
        assertNotSame(sequence, Sequence.valueOf(1));
    }

    @Test
    public void testToString() {
        assertNotNull(sequence.toString());
    }

}
