package org.memgraphd;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class GraphRequestTypeTest {

    @Test
    public void test() {
        assertNotNull(GraphRequestType.CREATE);
        assertNotNull(GraphRequestType.READ);
        assertNotNull(GraphRequestType.UPDATE);
        assertNotNull(GraphRequestType.DELETE);
    }

}
