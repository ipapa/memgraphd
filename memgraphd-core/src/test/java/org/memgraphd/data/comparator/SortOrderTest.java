package org.memgraphd.data.comparator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SortOrderTest {

    @Test
    public void testOrder() {
        assertEquals(1, SortOrder.ASCENDING.order());
        assertEquals(-1, SortOrder.DESCENDING.order());
    }

}
