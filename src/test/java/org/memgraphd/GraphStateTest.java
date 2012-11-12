package org.memgraphd;

import org.junit.Test;

import static org.junit.Assert.*;

public class GraphStateTest {

    @Test
    public void test() {
        assertNotNull(GraphState.INITIALIZED);
        assertNotNull(GraphState.RUNNING);
        assertNotNull(GraphState.STOPPED);
    }

}
