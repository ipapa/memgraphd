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
import org.memgraphd.data.OnlineVideo;
import org.memgraphd.data.TvEpisode;
import org.memgraphd.data.TvSeason;
import org.memgraphd.data.TvSeries;
import org.memgraphd.exception.GraphException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class GraphIT {
    private static final String TVSERIES_ID = "TvSeries-12345";
    private static final String TVSEASON_ID = "TvSeason-1";
    private static final String TVEPISODE_ID = "TvEpisode-123";
    private static final String VIDEO_ID = "Video-123";

    private final int CAPACITY = 1000;

    private Graph graph;
    private Data video, episode, season, series = null;

    @Before
    public void setUp() throws SQLException {

        video = new OnlineVideo(VIDEO_ID, new DateTime(), new DateTime(), "Video #1", TVEPISODE_ID);
        episode = new TvEpisode(TVEPISODE_ID, new DateTime(), new DateTime(), TVSEASON_ID,
                "Funny episode", "1", new DateTime());
        season = new TvSeason(TVSEASON_ID, new DateTime(), new DateTime(), "1", TVSERIES_ID);
        series = new TvSeries(TVSERIES_ID, new DateTime(), new DateTime(), "The Simpsons");
        
        graph =  new GraphImpl(CAPACITY);
        
        graph.start();
    }
    
    @After
    public void tearDown() {
        graph.clear();
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
    public void testGraphRelatedData() throws GraphException, SQLException {
        graph.write(video);
        graph.write(episode);
        graph.write(season);
        graph.write(series);

        GraphData graphVideo = graph.readId(VIDEO_ID);

        assertNotNull(graphVideo);
        assertSame(video, graphVideo.getData());
        assertSame(episode, graphVideo.getRelatedData().getLinks()
                .oneToOne(TvEpisode.class));
        assertNull(graphVideo.getRelatedData().getReferences());

        GraphData graphEpisode = graphVideo.getRelatedData().getLinks().relationships()[0];
        assertNotNull(graphEpisode);
        assertSame(season, graphEpisode.getRelatedData().getLinks()
                .oneToOne(TvSeason.class));
        assertSame(video, graphEpisode.getRelatedData().getReferences().oneToOne(OnlineVideo.class));

        GraphData graphSeason = graphEpisode.getRelatedData().getLinks().relationships()[0];
        assertNotNull(graphSeason);
        assertSame(series, graphSeason.getRelatedData().getLinks().oneToOne(TvSeries.class));
        assertSame(episode, graphSeason.getRelatedData().getReferences().oneToOne(TvEpisode.class));

        GraphData graphSeries = graphSeason.getRelatedData().getLinks().relationships()[0];
        assertNotNull(graphSeries);
        assertSame(season, graphSeries.getRelatedData().getReferences().oneToOne(TvSeason.class));
    }

    @Test
    public void testDeleteLinkInTheMiddleOfGraph() throws GraphException, SQLException,
            InterruptedException {

        graph.write(video);
        graph.write(episode);
        graph.write(season);
        graph.write(series);
        
        GraphData graphEpisode = graph.readId(TVEPISODE_ID);
        assertNotNull(graphEpisode);
        graph.delete(TVEPISODE_ID);
        
        GraphData graphVideo = graph.readId(VIDEO_ID);

        assertNull("I just deleted you", graph.readId(TVEPISODE_ID));
        assertNotNull(graphVideo);
        assertNull(graphVideo.getRelatedData().getLinks());
        assertNull(graphVideo.getRelatedData().getReferences());

        GraphData graphSeason = graph.readId(TVSEASON_ID);
        assertNotNull(graphSeason);
        assertNotNull(graphSeason.getRelatedData().getLinks());
        assertNotNull(graphSeason.getRelatedData().getLinks().relationships()[0]);
        assertNull(graphSeason.getRelatedData().getReferences());
    }

    @Test
    public void testSimpleDelete() throws GraphException, SQLException, InterruptedException {

        graph.write(video);

        GraphData graphVideo = graph.readId(VIDEO_ID);
        assertNotNull(graphVideo);
        assertSame(video, graphVideo.getData());

        graph.delete(VIDEO_ID);
        assertNull(graph.readId(VIDEO_ID));
    }

    @Test
    public void testDeleteTopReference() throws GraphException, SQLException, InterruptedException {

        graph.write(video);
        graph.write(episode);
        graph.write(season);
        graph.write(series);

        graph.delete(TVEPISODE_ID);
        graph.delete(TVSERIES_ID);

        assertNull(graph.readId(TVEPISODE_ID));
        assertNull(graph.readId(TVSERIES_ID));

        GraphData graphSeason = graph.readId(TVSEASON_ID);
        assertNull(graphSeason.getRelatedData().getLinks());
        assertNull(graphSeason.getRelatedData().getReferences());
    }

    @Test
    public void testGraphController() throws JSONException, GraphException, InterruptedException,
            SQLException {
        writeToGraph();
        assertEquals(CAPACITY, graph.occupied());
    }
    
    @Test
    public void testClearAllGraphData() throws JSONException, GraphException, InterruptedException,
            SQLException {
        testGraphController();
        
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
