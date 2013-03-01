package org.memgraphd;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.memgraphd.data.GraphData;
import org.memgraphd.exception.GraphException;
import org.memgraphd.memory.MemoryReference;
import org.memgraphd.test.data.TvEpisode;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

@Ignore
public class GraphPerformanceIT {

    private final int CAPACITY = 100000;

    private Graph graph;
    
    private TvEpisode episode;

    @Before
    public void setUp() throws Exception {
        
        graph =  GraphImpl.build(new GraphConfigDefaults("performanceGraph", CAPACITY));
        
        graph.run();
        
        episode = new TvEpisode("ep1", DateTime.now(), DateTime.now(), "season-1", null, "ep-1", DateTime.now());
    }
    
    @After
    public void tearDown() throws Exception {
        graph.clear();
        graph.shutdown();
    }
    
    @Test
    public void testCreate_emptyGraph() throws GraphException {
        
        long start = System.currentTimeMillis();
        graph.create(episode);
        start = System.currentTimeMillis() - start;
        
        System.out.println("Create took " + start + " milli secs");
        assertTrue("Create under 50 milli secs", start < 50);
    }
    
    @Test
    public void testReadId_emptyGraph() throws GraphException {
       
        graph.create(episode);
        
        long start = System.currentTimeMillis();
        GraphData gData = graph.read(episode.getId());
        start = System.currentTimeMillis() - start;
        
        System.out.println("Read took " + start + " milli secs");
        
        assertNotNull(gData);
        assertTrue("Read under 5 milli secs", start < 5);
        assertSame(episode, gData.getData());
    }
    
    @Test
    public void testReadReference_emptyGraph() throws GraphException {
     
        MemoryReference ref = graph.create(episode);
        
        long start = System.currentTimeMillis();
        GraphData gData = graph.read(ref);
        start = System.currentTimeMillis() - start;
        
        System.out.println("Read took " + start + " milli secs");
        
        assertNotNull(gData);
        assertTrue("Read under 5 milli secs", start < 5);
        assertSame(episode, gData.getData());
    }
    
    @Test
    public void testDeleteString_emptyGraph() throws GraphException {
     
        graph.create(episode);
        
        long start = System.currentTimeMillis();
        graph.delete(episode.getId());
        start = System.currentTimeMillis() - start;
        
        System.out.println("Delete took " + start + " milli secs");
        
        assertTrue("Delete under 5 milli secs", start < 5);
    }
    
    @Test
    public void testCreate_1000Graph() throws GraphException {
        
        long start = System.currentTimeMillis();
        createGraphEntries(1000);
        start = System.currentTimeMillis() - start;
        
        System.out.println("Create took " + start + " milli secs");
        assertTrue("Create 1000 entries under 1000 milli secs", start < 1000);
    }
    
    @Test
    public void testReadId_1000Graph() throws GraphException {
       
        createGraphEntries(1000);
        
        long start = System.currentTimeMillis();
        GraphData gData = graph.read("ep500");
        start = System.currentTimeMillis() - start;
        
        System.out.println("Read took " + start + " milli secs");
        
        assertNotNull(gData);
        assertTrue("Read under 5 milli secs", start < 5);
    }
    
    @Test
    public void testReadReference_1000Graph() throws GraphException {
        
        createGraphEntries(1000);

        long start = System.currentTimeMillis();
        GraphData gData = graph.read(MemoryReference.valueOf(500));
        start = System.currentTimeMillis() - start;
        
        System.out.println("Read took " + start + " milli secs");
        
        assertNotNull(gData);
        assertTrue("Read under 10 milli secs", start < 10);
    }
    
    @Test
    public void testDeleteString_1000Graph() throws GraphException {
     
        createGraphEntries(1000);
        
        long start = System.currentTimeMillis();
        graph.delete("ep500");
        start = System.currentTimeMillis() - start;
        
        System.out.println("Delete took " + start + " milli secs");
        
        assertTrue("Delete under 5 milli secs", start < 5);
    }
    
    @Test
    public void testCreate_100000Graph() throws GraphException {
        
        long start = System.currentTimeMillis();
        createGraphEntries(100000);
        start = System.currentTimeMillis() - start;
        
        System.out.println("Create took " + start + " milli secs");
        assertTrue("Create 100000 entries under 5000 milli secs", start < 5000);
    }
    
    @Test
    public void testReadId_100000Graph() throws GraphException {
       
        createGraphEntries(100000);
        
        long start = System.currentTimeMillis();
        GraphData gData = graph.read("ep50000");
        start = System.currentTimeMillis() - start;
        
        System.out.println("Read took " + start + " milli secs");
        
        assertNotNull(gData);
        assertTrue("Read under 5 milli secs", start < 5);
    }
    
    @Test
    public void testReadReference_100000Graph() throws GraphException {
        
        createGraphEntries(100000);

        long start = System.currentTimeMillis();
        GraphData gData = graph.read(MemoryReference.valueOf(50000));
        start = System.currentTimeMillis() - start;
        
        System.out.println("Read took " + start + " milli secs");
        
        assertNotNull(gData);
        assertTrue("Read under 10 milli secs", start < 10);
    }
    
    @Test
    public void testDeleteString_100000Graph() throws GraphException {
     
        createGraphEntries(100000);
        
        long start = System.currentTimeMillis();
        graph.delete("ep50000");
        start = System.currentTimeMillis() - start;
        
        System.out.println("Delete took " + start + " milli secs");
        
        assertTrue("Delete under 5 milli secs", start < 5);
    }
    
    private void createGraphEntries(int number) throws GraphException {
        for(int i=0; i < number; i++) {
            graph.create(new TvEpisode("ep" + i, DateTime.now(), DateTime.now(), "season-1", null, "ep-1", DateTime.now()));
        }
    }
}
