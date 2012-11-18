package org.memgraphd;

import java.sql.SQLException;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.memgraphd.data.Data;
import org.memgraphd.data.DataImpl;
import org.memgraphd.data.GraphData;
import org.memgraphd.exception.GraphException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class GraphIT {

    private final int CAPACITY = 1000;

    private Graph graph;

    @Before
    public void setUp() throws Exception {
        
        graph =  GraphImpl.build(new GraphConfigDefaults("someGraph", CAPACITY));
        
        graph.run();
    }
    
    @After
    public void tearDown() throws Exception {
        graph.clear();
        graph.shutdown();
    }
    
    @Test
    public void testGraph_write() throws GraphException, SQLException, JSONException {
        writeToGraph();
        assertEquals(CAPACITY, graph.capacity());
        assertEquals(CAPACITY, graph.occupied());
        assertEquals(0, graph.available());
        assertEquals(0, graph.recycled());
    }
    
    @Test
    public void testGraph_readId() throws GraphException, SQLException, JSONException {
        testGraph_write();
        for (int i = 0; i < CAPACITY; i++) {
            assertNotNull(graph.readId(String.valueOf(i)));
            assertEquals(String.valueOf(i), graph.readId(String.valueOf(i)).getData().getId());
        }
    }
    
    @Test
    public void testGraph_readSequence() throws GraphException, SQLException, JSONException {
        testGraph_write();
        for (int i = 0; i < CAPACITY; i++) {
            GraphData gData = graph.readId(String.valueOf(i));
            assertNotNull(gData);
            GraphData seqGData = graph.readSequence(gData.getSequence());
            assertSame(gData, seqGData);
        }
    }
    
    @Test
    public void testGraph_delete() throws GraphException, SQLException, JSONException {
        testGraph_write();
        
        for (int i = 0; i < CAPACITY; i++) {
            graph.delete(String.valueOf(i));
            assertNull(graph.readId(String.valueOf(i)));
        }
        
        assertEquals(CAPACITY, graph.capacity());
        assertEquals(0, graph.occupied());
        assertEquals(CAPACITY, graph.available());
        assertEquals(CAPACITY, graph.recycled());
    }

    @Test
    public void testGraph() throws JSONException, GraphException, InterruptedException,
            SQLException {
        writeToGraph();
        assertEquals(CAPACITY, graph.occupied());
    }
    
    @Test
    public void testClearAllGraphData() throws JSONException, GraphException, InterruptedException,
            SQLException {
        testGraph();
        
        graph.clear();
        
        assertEquals(0, graph.occupied());
        assertEquals(CAPACITY, graph.capacity());
        assertEquals(CAPACITY, graph.available());
    }

    private void writeToGraph()
            throws GraphException, JSONException {

        for (int i = 0; i < CAPACITY; i++) {
           Data data = new DataImpl(String.valueOf(i), new DateTime(), new DateTime());
           graph.write(data);
        }
    }
}
