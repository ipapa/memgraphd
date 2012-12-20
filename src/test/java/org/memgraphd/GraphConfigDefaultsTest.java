package org.memgraphd;

import org.junit.Before;
import org.junit.Test;
import org.memgraphd.bookkeeper.HSQLBookKeeper;
import org.memgraphd.decision.SingleDecisionMaker;
import org.memgraphd.memory.DefaultMemoryBlockResolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class GraphConfigDefaultsTest {
    private GraphConfig configZero, configOne, configTwo, configThree, configFour;
    
    @Before
    public void setUp() throws Exception {
        configZero = new GraphConfigDefaults();
        configOne = new GraphConfigDefaults("name");
        configTwo = new GraphConfigDefaults("name", 1);
        configThree = new GraphConfigDefaults("name", 1, "dbName");
        configFour = new GraphConfigDefaults("name", 1, "dbName", "/tmp/dbPath");
    }

    @Test
    public void testGraphConfigDefaults() {
        assertNotNull(configZero);
        assertNotNull(configOne);
        assertNotNull(configTwo);
        assertNotNull(configThree);
        assertNotNull(configFour);
    }

    @Test
    public void testGetName() {
        assertEquals(GraphConfig.DEFAULT_NAME, configZero.getName());
        assertEquals("name", configOne.getName());
        assertEquals("name", configTwo.getName());
        assertEquals("name", configThree.getName());
        assertEquals("name", configFour.getName());
    }

    @Test
    public void testGetCapacity() {
        assertEquals(GraphConfig.DEFAULT_CAPACITY, configZero.getCapacity());
        assertEquals(GraphConfig.DEFAULT_CAPACITY, configOne.getCapacity());
        assertEquals(1, configTwo.getCapacity());
        assertEquals(1, configThree.getCapacity());
        assertEquals(1, configFour.getCapacity());
    }

    @Test
    public void testGetBookKeeperDatabaseName() {
        assertEquals(GraphConfig.DEFAULT_DB_NAME, configZero.getBookKeeperDatabaseName());
        assertEquals(GraphConfig.DEFAULT_DB_NAME, configOne.getBookKeeperDatabaseName());
        assertEquals(GraphConfig.DEFAULT_DB_NAME, configTwo.getBookKeeperDatabaseName());
        assertEquals("dbName", configThree.getBookKeeperDatabaseName());
        assertEquals("dbName", configFour.getBookKeeperDatabaseName());
    }

    @Test
    public void testGetBookKeeperDatabasePath() {
        assertEquals(GraphConfig.DEFAULT_DB_PATH, configZero.getBookKeeperDatabasePath());
        assertEquals(GraphConfig.DEFAULT_DB_PATH, configOne.getBookKeeperDatabasePath());
        assertEquals(GraphConfig.DEFAULT_DB_PATH, configTwo.getBookKeeperDatabasePath());
        assertEquals(GraphConfig.DEFAULT_DB_PATH, configThree.getBookKeeperDatabasePath());
        assertEquals("/tmp/dbPath", configFour.getBookKeeperDatabasePath());
    }

    @Test
    public void testGetMemoryBlockResolver() {
        assertNotNull(configZero.getMemoryBlockResolver());
        assertTrue(configZero.getMemoryBlockResolver() instanceof DefaultMemoryBlockResolver);
        
        assertNotNull(configOne.getMemoryBlockResolver());
        assertTrue(configOne.getMemoryBlockResolver() instanceof DefaultMemoryBlockResolver);
        
        assertNotNull(configTwo.getMemoryBlockResolver());
        assertTrue(configTwo.getMemoryBlockResolver() instanceof DefaultMemoryBlockResolver);
        
        assertNotNull(configThree.getMemoryBlockResolver());
        assertTrue(configThree.getMemoryBlockResolver() instanceof DefaultMemoryBlockResolver);
        
        assertNotNull(configFour.getMemoryBlockResolver());
        assertTrue(configFour.getMemoryBlockResolver() instanceof DefaultMemoryBlockResolver);
    }

    @Test
    public void testGetDecisionMaker() {
        assertNotNull(configZero.getDecisionMaker());
        assertTrue(configZero.getDecisionMaker() instanceof SingleDecisionMaker);
        
        assertNotNull(configOne.getDecisionMaker());
        assertTrue(configOne.getDecisionMaker() instanceof SingleDecisionMaker);
        
        assertNotNull(configTwo.getDecisionMaker());
        assertTrue(configTwo.getDecisionMaker() instanceof SingleDecisionMaker);
        
        assertNotNull(configThree.getDecisionMaker());
        assertTrue(configThree.getDecisionMaker() instanceof SingleDecisionMaker);
        
        assertNotNull(configFour.getDecisionMaker());
        assertTrue(configFour.getDecisionMaker() instanceof SingleDecisionMaker);
    }

    @Test
    public void testGetBookKeeper() {
        assertNotNull(configZero.getBookKeeper());
        assertTrue(configZero.getBookKeeper() instanceof HSQLBookKeeper);
        
        assertNotNull(configOne.getBookKeeper());
        assertTrue(configOne.getBookKeeper() instanceof HSQLBookKeeper);
        
        assertNotNull(configTwo.getBookKeeper());
        assertTrue(configTwo.getBookKeeper() instanceof HSQLBookKeeper);
        
        assertNotNull(configThree.getBookKeeper());
        assertTrue(configThree.getBookKeeper() instanceof HSQLBookKeeper);
        
        assertNotNull(configFour.getBookKeeper());
        assertTrue(configFour.getBookKeeper() instanceof HSQLBookKeeper);
    }

}
