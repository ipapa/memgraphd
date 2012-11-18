package org.memgraphd;

import org.junit.Before;
import org.junit.Test;
import org.memgraphd.bookkeeper.HSQLBookKeeper;
import org.memgraphd.decision.SingleDecisionMaker;
import org.memgraphd.memory.DefaultMemoryBlockResolver;

import static org.junit.Assert.*;

public class GraphConfigDefaultsTest {
    private GraphConfig config;
    
    @Before
    public void setUp() throws Exception {
        config = new GraphConfigDefaults();
    }

    @Test
    public void testGraphConfigDefaults() {
        assertNotNull(config);
    }

    @Test
    public void testGetName() {
        assertEquals(GraphConfig.DEFAULT_NAME, config.getName());
    }

    @Test
    public void testGetCapacity() {
        assertEquals(GraphConfig.DEFAULT_CAPACITY, config.getCapacity());
    }

    @Test
    public void testGetBookKeeperDatabaseName() {
        assertEquals(GraphConfig.DEFAULT_DB_NAME, config.getBookKeeperDatabaseName());
    }

    @Test
    public void testGetBookKeeperDatabasePath() {
        assertEquals(GraphConfig.DEFAULT_DB_PATH, config.getBookKeeperDatabasePath());
    }

    @Test
    public void testGetMemoryBlockResolver() {
        assertNotNull(config.getMemoryBlockResolver());
        assertTrue(config.getMemoryBlockResolver() instanceof DefaultMemoryBlockResolver);
    }

    @Test
    public void testGetDecisionMaker() {
        assertNotNull(config.getDecisionMaker());
        assertTrue(config.getDecisionMaker() instanceof SingleDecisionMaker);
    }

    @Test
    public void testGetBookKeeper() {
        assertNotNull(config.getBookKeeper());
        assertTrue(config.getBookKeeper() instanceof HSQLBookKeeper);
    }

}
