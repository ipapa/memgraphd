package org.memgraphd;

import org.joda.time.DateTime;
import org.junit.Test;
import org.memgraphd.data.Data;
import org.memgraphd.data.DataImpl;
import org.memgraphd.exception.GraphException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class GraphDataSnapshotManagerIT {
    private static Graph graph;
    private static Data data1 = new DataImpl("id-1", DateTime.now(), DateTime.now());
    private static Data data2 = new DataImpl("id-2", DateTime.now(), DateTime.now());
    private static Data data3 = new DataImpl("id-3", DateTime.now(), DateTime.now());
    private static Data data4 = new DataImpl("id-4", DateTime.now(), DateTime.now());
    private static Data data5 = new DataImpl("id-5", DateTime.now(), DateTime.now());

    @Test
    public void testGraphInitializedAndEmpty() throws GraphException {
        graph = GraphImpl.build(5);
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
//        graph = GraphImpl.build(5);
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
        assertNotNull(graph.write(data1));
        assertNotNull(graph.write(data2));
        assertNotNull(graph.write(data3));
        assertNotNull(graph.write(data4));
        assertNotNull(graph.write(data5));
    }

    private void chekGraphStats() {
        assertFalse(graph.isEmpty());
        assertEquals(5, graph.capacity());
        assertEquals(0, graph.available());
        assertEquals(5, graph.occupied());
        assertEquals(0, graph.recycled());
    }

    private void readDataFromGraph() {
        compareData(data1, graph.readId(data1.getId()).getData());
        compareData(data2, graph.readId(data2.getId()).getData());
        compareData(data3, graph.readId(data3.getId()).getData());
        compareData(data4, graph.readId(data4.getId()).getData());
        compareData(data5, graph.readId(data5.getId()).getData());
    }
    
    private void compareData(Data original, Data newData) {
        assertNotNull(original);
        assertNotNull(newData);
        assertEquals(original.getId(), newData.getId());
        assertEquals(original.getCreatedDate(), newData.getCreatedDate());
        assertEquals(original.getLastModifiedDate(), newData.getLastModifiedDate());
    }
}
