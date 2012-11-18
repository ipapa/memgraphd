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

        video = new OnlineVideo(VIDEO_ID, new DateTime(), new DateTime(), "Video #1", TVEPISODE_ID);
        video2 = new OnlineVideo(VIDEO_ID + 1, new DateTime(), new DateTime(), "Video #1", TVEPISODE_ID);
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
    public void testOneLinkRelationship_writesReverseOrder() throws GraphException {
        graph.write(episode);
        graph.write(video);
        
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
    public void testOneToManyRelationship_writesReverseOrder() throws GraphException {
        graph.write(episode);
        graph.write(video);
        graph.write(video2);
        
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
    public void testBigTreeRelationship() throws GraphException {
        graph.write(network);
        graph.write(video);
        graph.write(episode);
        graph.write(season);
        graph.write(series);
        graph.write(movie1);
        graph.write(movie2);
        
        GraphData networkData = graph.readId(network.getId());
        assertNotNull(networkData);
        Set<Movie> hboMovies = networkData.getRelatedData().getReferences().oneToMany(Movie.class);
        assertNotNull(hboMovies);
        assertTrue(hboMovies.contains(movie1));
        assertTrue(hboMovies.contains(movie2));
        assertSame(series, networkData.getRelatedData().getReferences().oneToOne(TvSeries.class));
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
    
    @Test(expected=GraphException.class)
    public void testDelete_dataDoesNotExist() throws GraphException, SQLException, InterruptedException {
        graph.delete(VIDEO_ID);
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
