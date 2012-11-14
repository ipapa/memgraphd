package org.memgraphd;

import java.sql.SQLException;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.memgraphd.data.Data;
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
import static org.junit.Assert.assertTrue;

public class GraphRelationshipsIT {
    private static final String TVSERIES_ID = "TvSeries-12345";
    private static final String TVSEASON_ID = "TvSeason-1";
    private static final String TVEPISODE_ID = "TvEpisode-123";
    private static final String VIDEO_ID = "Video-123";

    private final int CAPACITY = 1000;

    private Graph graph;
    private Data video, video2, episode, season, series = null;

    @Before
    public void setUp() throws SQLException {

        video = new OnlineVideo(VIDEO_ID, new DateTime(), new DateTime(), "Video #1", TVEPISODE_ID);
        video2 = new OnlineVideo(VIDEO_ID + 1, new DateTime(), new DateTime(), "Video #1", TVEPISODE_ID);
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
        graph.stop();
    }
    
    @Test
    public void testNoRelationships() throws GraphException {
        graph.write(video);
        
        GraphData videoGraph = graph.readId(video.getId());
        assertNotNull(videoGraph);
        assertNotNull(videoGraph.getRelatedData());
        assertNull(videoGraph.getRelatedData().getLinks());
        assertNull(videoGraph.getRelatedData().getReferences());
    }
    
    @Test
    public void testOneLinkRelationship() throws GraphException {
        graph.write(video);
        graph.write(episode);
        
        GraphData videoGraph = graph.readId(video.getId());
        assertNotNull(videoGraph);
        assertNotNull(videoGraph.getRelatedData());
        assertNotNull(videoGraph.getRelatedData().getLinks());
        assertEquals(1, videoGraph.getRelatedData().getLinks().relationships().length);
        assertSame(episode, videoGraph.getRelatedData().getLinks().oneToOne(TvEpisode.class));
        
        GraphData episodeGraph = graph.readId(episode.getId());
        assertNotNull(episodeGraph);
        assertNotNull(episodeGraph.getRelatedData());
        assertNull(episodeGraph.getRelatedData().getLinks());
        assertNotNull(episodeGraph.getRelatedData().getReferences());
        assertEquals(1, episodeGraph.getRelatedData().getReferences().relationships().length);
        assertSame(video, episodeGraph.getRelatedData().getReferences().oneToOne(OnlineVideo.class));
    }
    
    @Test
    public void testOneToManyRelationship() throws GraphException {
        graph.write(video);
        graph.write(video2);
        graph.write(episode);
        
        GraphData videoGraph = graph.readId(video.getId());
        assertNotNull(videoGraph);
        assertNotNull(videoGraph.getRelatedData());
        assertNotNull(videoGraph.getRelatedData().getLinks());
        assertEquals(1, videoGraph.getRelatedData().getLinks().relationships().length);
        assertSame(episode, videoGraph.getRelatedData().getLinks().oneToOne(TvEpisode.class));
        
        GraphData videoGraph2 = graph.readId(video2.getId());
        assertNotNull(videoGraph2);
        assertNotNull(videoGraph2.getRelatedData());
        assertNotNull(videoGraph2.getRelatedData().getLinks());
        assertEquals(1, videoGraph2.getRelatedData().getLinks().relationships().length);
        assertSame(episode, videoGraph2.getRelatedData().getLinks().oneToOne(TvEpisode.class));
        
        GraphData episodeGraph = graph.readId(episode.getId());
        assertNotNull(episodeGraph);
        assertNotNull(episodeGraph.getRelatedData());
        assertNull(episodeGraph.getRelatedData().getLinks());
        assertNotNull(episodeGraph.getRelatedData().getReferences());
        assertEquals(2, episodeGraph.getRelatedData().getReferences().relationships().length);
        assertTrue(episodeGraph.getRelatedData().getReferences().oneToMany(OnlineVideo.class).contains(video));
        assertTrue(episodeGraph.getRelatedData().getReferences().oneToMany(OnlineVideo.class).contains(video2));
    }
    
    
    @Test
    public void testTreeRelationship() throws GraphException, SQLException {
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
    public void testRelationshipOnDelete() throws GraphException {
        graph.write(video);
        graph.write(episode);
        
        graph.delete(episode.getId());
        assertNull(graph.readId(episode.getId()));
        GraphData videoGraph = graph.readId(video.getId());
        assertNull(videoGraph.getRelatedData().getLinks());
        assertNull(videoGraph.getRelatedData().getReferences());
        
        graph.write(episode);
        videoGraph = graph.readId(video.getId());
        assertSame(episode, videoGraph.getRelatedData().getLinks().oneToOne(TvEpisode.class));
        assertNull(videoGraph.getRelatedData().getReferences());
        
        GraphData episodeGraph = graph.readId(episode.getId());
        assertSame(video, episodeGraph.getRelatedData().getReferences().oneToOne(OnlineVideo.class));
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
}
