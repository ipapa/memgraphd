package org.memgraphd;

import java.sql.SQLException;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.memgraphd.data.Data;
import org.memgraphd.data.GraphData;
import org.memgraphd.exception.GraphException;
import org.memgraphd.test.data.Movie;
import org.memgraphd.test.data.OnlineVideo;
import org.memgraphd.test.data.TvEpisode;
import org.memgraphd.test.data.TvNetwork;
import org.memgraphd.test.data.TvSeason;
import org.memgraphd.test.data.TvSeries;

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
    private static final String NETWORK_ID = "NETWORK-123";
    private static final String NETWORK_NAME = "HBO";

    private final int CAPACITY = 1000;

    private Graph graph;
    private Data video, video2, episode, season, series, movie1, movie2, network = null;

    @Before
    public void setUp() throws SQLException, GraphException {

        video = new OnlineVideo(VIDEO_ID, new DateTime(), new DateTime(), "Video #1", TVEPISODE_ID, true);
        video2 = new OnlineVideo(VIDEO_ID + 1, new DateTime(), new DateTime(), "Video #1", TVEPISODE_ID, true);
        episode = new TvEpisode(TVEPISODE_ID, new DateTime(), new DateTime(), TVSEASON_ID,
                "Funny episode", "1", new DateTime());
        season = new TvSeason(TVSEASON_ID, new DateTime(), new DateTime(), "1", TVSERIES_ID);
        series = new TvSeries(TVSERIES_ID, NETWORK_ID, new DateTime(), new DateTime(), "The Simpsons");
        network = new TvNetwork(NETWORK_ID, NETWORK_NAME, new DateTime(), new DateTime());
        movie1 = new Movie("Movie-1", "Gone with the wind", NETWORK_ID, new DateTime(), new DateTime());
        movie2 = new Movie("Movie-2", "Batman", NETWORK_ID, new DateTime(), new DateTime());
        
        graph =  GraphImpl.build(new GraphConfigDefaults("myGraph", CAPACITY));
        
        graph.run();
    }
    
    @After
    public void tearDown() throws GraphException {
        graph.clear();
        graph.shutdown();
    }
    
    @Test
    public void testNoRelationships() throws GraphException {
        graph.create(video);
        
        GraphData videoGraph = graph.readGraph(video.getId());
        assertNotNull(videoGraph);
        assertNotNull(videoGraph.getRelatedData());
        assertNull(videoGraph.getRelatedData().getLinks());
        assertNull(videoGraph.getRelatedData().getReferences());
    }
    
    @Test
    public void testOneLinkRelationship() throws GraphException {
        graph.create(video);
        graph.create(episode);
        
        GraphData videoGraph = graph.readGraph(video.getId());
        assertNotNull(videoGraph);
        assertNotNull(videoGraph.getRelatedData());
        assertNotNull(videoGraph.getRelatedData().getLinks());
        assertEquals(1, videoGraph.getRelatedData().getLinks().relationships().length);
        assertSame(episode, videoGraph.getRelatedData().getLinks().oneToOne(TvEpisode.class));
        
        GraphData episodeGraph = graph.read(episode.getId());
        assertNotNull(episodeGraph);
        assertNotNull(episodeGraph.getRelatedData());
        assertNull(episodeGraph.getRelatedData().getLinks());
        assertNotNull(episodeGraph.getRelatedData().getReferences());
        assertEquals(1, episodeGraph.getRelatedData().getReferences().relationships().length);
        assertSame(video, episodeGraph.getRelatedData().getReferences().oneToOne(OnlineVideo.class));
    }
    
    @Test
    public void testOneLinkRelationship_writesReverseOrder() throws GraphException {
        graph.create(episode);
        graph.create(video);
        
        GraphData videoGraph = graph.readGraph(video.getId());
        assertNotNull(videoGraph);
        assertNotNull(videoGraph.getRelatedData());
        assertNotNull(videoGraph.getRelatedData().getLinks());
        assertEquals(1, videoGraph.getRelatedData().getLinks().relationships().length);
        assertSame(episode, videoGraph.getRelatedData().getLinks().oneToOne(TvEpisode.class));
        
        GraphData episodeGraph = graph.read(episode.getId());
        assertNotNull(episodeGraph);
        assertNotNull(episodeGraph.getRelatedData());
        assertNull(episodeGraph.getRelatedData().getLinks());
        assertNotNull(episodeGraph.getRelatedData().getReferences());
        assertEquals(1, episodeGraph.getRelatedData().getReferences().relationships().length);
        assertSame(video, episodeGraph.getRelatedData().getReferences().oneToOne(OnlineVideo.class));
    }
    
    @Test
    public void testOneToManyRelationship() throws GraphException {
        graph.create(video);
        graph.create(video2);
        graph.create(episode);
        
        GraphData videoGraph = graph.readGraph(video.getId());
        assertNotNull(videoGraph);
        assertNotNull(videoGraph.getRelatedData());
        assertNotNull(videoGraph.getRelatedData().getLinks());
        assertEquals(1, videoGraph.getRelatedData().getLinks().relationships().length);
        assertSame(episode, videoGraph.getRelatedData().getLinks().oneToOne(TvEpisode.class));
        
        GraphData videoGraph2 = graph.readGraph(video2.getId());
        assertNotNull(videoGraph2);
        assertNotNull(videoGraph2.getRelatedData());
        assertNotNull(videoGraph2.getRelatedData().getLinks());
        assertEquals(1, videoGraph2.getRelatedData().getLinks().relationships().length);
        assertSame(episode, videoGraph2.getRelatedData().getLinks().oneToOne(TvEpisode.class));
        
        GraphData episodeGraph = graph.readGraph(episode.getId());
        assertNotNull(episodeGraph);
        assertNotNull(episodeGraph.getRelatedData());
        assertNull(episodeGraph.getRelatedData().getLinks());
        assertNotNull(episodeGraph.getRelatedData().getReferences());
        assertEquals(2, episodeGraph.getRelatedData().getReferences().relationships().length);
        assertTrue(episodeGraph.getRelatedData().getReferences().oneToMany(OnlineVideo.class).contains(video));
        assertTrue(episodeGraph.getRelatedData().getReferences().oneToMany(OnlineVideo.class).contains(video2));
    }
    
    @Test
    public void testOneToManyRelationship_writesReverseOrder() throws GraphException {
        graph.create(episode);
        graph.create(video);
        graph.create(video2);
        
        GraphData videoGraph = graph.readGraph(video.getId());
        assertNotNull(videoGraph);
        assertNotNull(videoGraph.getRelatedData());
        assertNotNull(videoGraph.getRelatedData().getLinks());
        assertEquals(1, videoGraph.getRelatedData().getLinks().relationships().length);
        assertSame(episode, videoGraph.getRelatedData().getLinks().oneToOne(TvEpisode.class));
        
        GraphData videoGraph2 = graph.readGraph(video2.getId());
        assertNotNull(videoGraph2);
        assertNotNull(videoGraph2.getRelatedData());
        assertNotNull(videoGraph2.getRelatedData().getLinks());
        assertEquals(1, videoGraph2.getRelatedData().getLinks().relationships().length);
        assertSame(episode, videoGraph2.getRelatedData().getLinks().oneToOne(TvEpisode.class));
        
        GraphData episodeGraph = graph.readGraph(episode.getId());
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
        graph.create(video);
        graph.create(episode);
        graph.create(season);
        graph.create(series);

        GraphData graphVideo = graph.readGraph(VIDEO_ID);

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
    public void testBigTreeRelationship() throws GraphException {
        graph.create(network);
        graph.create(video);
        graph.create(episode);
        graph.create(season);
        graph.create(series);
        graph.create(movie1);
        graph.create(movie2);
        
        GraphData networkData = graph.readGraph(network.getId());
        assertNotNull(networkData);
        Set<Movie> hboMovies = networkData.getRelatedData().getReferences().oneToMany(Movie.class);
        assertNotNull(hboMovies);
        assertTrue(hboMovies.contains(movie1));
        assertTrue(hboMovies.contains(movie2));
        assertSame(series, networkData.getRelatedData().getReferences().oneToOne(TvSeries.class));
    }
    
    @Test
    public void testRelationshipOnDelete() throws GraphException {
        graph.create(video);
        graph.create(episode);
        
        graph.delete(episode.getId());
        assertNull(graph.readGraph(episode.getId()));
        GraphData videoGraph = graph.readGraph(video.getId());
        assertNull(videoGraph.getRelatedData().getLinks());
        assertNull(videoGraph.getRelatedData().getReferences());
        
        graph.create(episode);
        videoGraph = graph.readGraph(video.getId());
        assertSame(episode, videoGraph.getRelatedData().getLinks().oneToOne(TvEpisode.class));
        assertNull(videoGraph.getRelatedData().getReferences());
        
        GraphData episodeGraph = graph.readGraph(episode.getId());
        assertSame(video, episodeGraph.getRelatedData().getReferences().oneToOne(OnlineVideo.class));
    }
    
    @Test
    public void testSimpleDelete() throws GraphException, SQLException, InterruptedException {

        graph.create(video);

        GraphData graphVideo = graph.readGraph(VIDEO_ID);
        assertNotNull(graphVideo);
        assertSame(video, graphVideo.getData());

        graph.delete(VIDEO_ID);
        assertNull(graph.readGraph(VIDEO_ID));
    }
    
    @Test(expected=GraphException.class)
    public void testDelete_dataDoesNotExist() throws GraphException, SQLException, InterruptedException {
        graph.delete(VIDEO_ID);
    }

    @Test
    public void testDeleteTopReference() throws GraphException, SQLException, InterruptedException {

        graph.create(video);
        graph.create(episode);
        graph.create(season);
        graph.create(series);

        graph.delete(TVEPISODE_ID);
        graph.delete(TVSERIES_ID);

        assertNull(graph.readGraph(TVEPISODE_ID));
        assertNull(graph.readGraph(TVSERIES_ID));

        GraphData graphSeason = graph.readGraph(TVSEASON_ID);
        assertNull(graphSeason.getRelatedData().getLinks());
        assertNull(graphSeason.getRelatedData().getReferences());
    }
    
    @Test
    public void testDeleteReference() throws GraphException, SQLException,
            InterruptedException {

        graph.create(video);
        graph.create(episode);
   
        graph.delete(VIDEO_ID);
        
        assertNull(graph.readGraph(VIDEO_ID));

        GraphData graphEpisode = graph.readGraph(episode.getId());
        assertNotNull(graphEpisode);
        assertNull(graphEpisode.getRelatedData().getLinks());
        assertNull(graphEpisode.getRelatedData().getReferences());
    }
    
    @Test
    public void testWriteDeleteWrite() throws GraphException, SQLException,
            InterruptedException {

        graph.create(video);
        graph.create(episode);
   
        graph.delete(VIDEO_ID);
        
        assertNull(graph.readGraph(VIDEO_ID));

        GraphData graphEpisode = graph.readGraph(episode.getId());
        assertNotNull(graphEpisode);
        assertNull(graphEpisode.getRelatedData().getLinks());
        assertNull(graphEpisode.getRelatedData().getReferences());
        
        // write video again
        graph.create(video);
        
        GraphData videoGraph = graph.readGraph(video.getId());
        assertSame(episode, videoGraph.getRelatedData().getLinks().oneToOne(TvEpisode.class));
        assertNull(videoGraph.getRelatedData().getReferences());
        
        GraphData episodeGraph = graph.readGraph(episode.getId());
        assertSame(video, episodeGraph.getRelatedData().getReferences().oneToOne(OnlineVideo.class));
    }
    
    @Test
    public void testDeleteLinkInTheMiddleOfGraph() throws GraphException, SQLException,
            InterruptedException {

        graph.create(video);
        graph.create(episode);
        graph.create(season);
        graph.create(series);
        
        GraphData graphEpisode = graph.read(TVEPISODE_ID);
        assertNotNull(graphEpisode);
        graph.delete(TVEPISODE_ID);
        
        GraphData graphVideo = graph.readGraph(VIDEO_ID);

        assertNull("I just deleted you", graph.readGraph(TVEPISODE_ID));
        assertNotNull(graphVideo);
        assertNull(graphVideo.getRelatedData().getLinks());
        assertNull(graphVideo.getRelatedData().getReferences());

        GraphData graphSeason = graph.readGraph(TVSEASON_ID);
        assertNotNull(graphSeason);
        assertNotNull(graphSeason.getRelatedData().getLinks());
        assertNotNull(graphSeason.getRelatedData().getLinks().relationships()[0]);
        assertNull(graphSeason.getRelatedData().getReferences());
    }
}
