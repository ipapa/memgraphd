package org.memgraphd;

import java.sql.SQLException;

import org.joda.time.DateTime;
import org.junit.Test;
import org.memgraphd.data.Data;
import org.memgraphd.data.ReadWriteData;
import org.memgraphd.exception.GraphException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class GraphDataSnapshotManagerIT {
    private static Graph graph;
    private static GraphConfig config = createConfig();
    private static Data data1 = new ReadWriteData("id-1", DateTime.now(), DateTime.now());
    private static Data data2 = new ReadWriteData("id-2", DateTime.now(), DateTime.now());
    private static Data data3 = new ReadWriteData("id-3", DateTime.now(), DateTime.now());
    private static Data data4 = new ReadWriteData("id-4", DateTime.now(), DateTime.now());
    private static Data data5 = new ReadWriteData("id-5", DateTime.now(), DateTime.now());
    
    private static GraphConfig createConfig() {
        try {
            return new GraphConfigDefaults("someGraph", 5);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    public void testGraphInitializedAndEmpty() throws GraphException {
        graph = GraphImpl.build(config);
        assertTrue(graph.isInitialized());
        assertFalse(graph.isRunning());
        assertFalse(graph.isShutdown());
        assertTrue(graph.isEmpty());
    }
    
    @Test
    public void testGraphRunning() throws GraphException {
        graph.run();
        assertTrue(graph.isRunning());
        assertTrue(graph.isInitialized());
        assertFalse(graph.isShutdown());
    }
    
    @Test
    public void testGraphWrite() throws GraphException, InterruptedException {
        writeDataToGraph();
        Thread.sleep(5 * 1000L);
        chekGraphStats();
    }
    
    @Test
    public void testGraphRead() throws GraphException {
        readDataFromGraph();
    }
    
    @Test
    public void testGraphShutdown() throws GraphException {
        graph.shutdown();
        assertTrue(graph.isShutdown());
        assertFalse(graph.isRunning());
        assertFalse(graph.isInitialized());
    }
    
    @Test
    public void testGraphRestart() throws GraphException {
        graph.run();
        chekGraphStats();
        readDataFromGraph();
    }
    
    @Test
    public void testClear() throws GraphException {
        graph.clear();
        assertTrue(graph.isEmpty());
        assertTrue(graph.isRunning());
        assertTrue(graph.isInitialized());
        assertFalse(graph.isShutdown());
    }
    
    private void writeDataToGraph() throws GraphException {
        assertNotNull(graph.create(data1));
        assertNotNull(graph.create(data2));
        assertNotNull(graph.create(data3));
        assertNotNull(graph.create(data4));
        assertNotNull(graph.create(data5));
    }

    private void chekGraphStats() {
        assertFalse(graph.isEmpty());
        assertEquals(5, graph.capacity());
        assertEquals(0, graph.available());
        assertEquals(5, graph.occupied());
        assertEquals(0, graph.recycled());
    }

    private void readDataFromGraph() {
        compareData(data1, graph.read(data1.getId()).getData());
        compareData(data2, graph.read(data2.getId()).getData());
        compareData(data3, graph.read(data3.getId()).getData());
        compareData(data4, graph.read(data4.getId()).getData());
        compareData(data5, graph.read(data5.getId()).getData());
    }
    
    private void compareData(Data original, Data newData) {
        assertNotNull(original);
        assertNotNull(newData);
        assertEquals(original.getId(), newData.getId());
        assertEquals(original.getCreatedDate(), newData.getCreatedDate());
        assertEquals(original.getLastModifiedDate(), newData.getLastModifiedDate());
    }
}
