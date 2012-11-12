package org.memgraphd;

import org.junit.Test;

import static org.junit.Assert.*;

public class GraphRequestTypeTest {

    @Test
    public void test() {
        assertNotNull(GraphRequestType.PUT);
        assertNotNull(GraphRequestType.DELETE);
        assertNotNull(GraphRequestType.GET);
    }

}
