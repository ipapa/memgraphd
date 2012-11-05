package org.memgraphd;

import java.sql.SQLException;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.memgraphd.bookkeeper.HSQLBookKeeper;
import org.memgraphd.controller.GraphControllerImpl;
import org.memgraphd.data.Data;
import org.memgraphd.data.DataImpl;
import org.memgraphd.data.GraphData;
import org.memgraphd.data.OnlineVideo;
import org.memgraphd.data.TvEpisode;
import org.memgraphd.data.TvSeason;
import org.memgraphd.data.TvSeries;
import org.memgraphd.decision.Decision;
import org.memgraphd.decision.DecisionImpl;
import org.memgraphd.decision.Sequence;
import org.memgraphd.decision.SingleNodeDecisionMaker;
import org.memgraphd.exception.GraphException;
import org.memgraphd.memory.MemoryBlock;
import org.memgraphd.memory.MemoryBlockImpl;
import org.memgraphd.memory.MemoryBlockResolver;
import org.memgraphd.memory.MemoryManager;
import org.memgraphd.memory.MemoryManagerImpl;
import org.memgraphd.memory.MemoryReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class GraphIT {
    private static final String TVSERIES_ID = "TvSeries-12345";
    private static final String TVSEASON_ID = "TvSeason-1";
    private static final String TVEPISODE_ID = "TvEpisode-123";
    private static final String VIDEO_ID = "Video-123";
    private static final String DB_PATH = "/tmp/book/";
    private static final String DB_NAME = "public.book";

    private final int CAPACITY = 100;

    private Graph graph;
    private GraphControllerImpl graphController;
    private MemoryManager memoryManager;
    private SingleNodeDecisionMaker decisionMaker;
    private Data video, episode, season, series = null;

    private Decision decisionVideo, decisionEpisode, decisionSeason, decisionSeries = null;
    
    private long start;

    @Before
    public void setUp() throws SQLException {

        initializeGraph();

        graph.start();

        video = new OnlineVideo(VIDEO_ID, new DateTime(), new DateTime(), "Video #1", TVEPISODE_ID);
        episode = new TvEpisode(TVEPISODE_ID, new DateTime(), new DateTime(), TVSEASON_ID,
                "Funny episode", "1", new DateTime());
        season = new TvSeason(TVSEASON_ID, new DateTime(), new DateTime(), "1", TVSERIES_ID);
        series = new TvSeries(TVSERIES_ID, new DateTime(), new DateTime(), "The Simpsons");
        
        DateTime now = new DateTime();
        decisionVideo = new DecisionImpl(Sequence.valueOf(1), now, GraphRequestType.PUT, video.getId(), video);
        decisionEpisode = new DecisionImpl(Sequence.valueOf(2), now, GraphRequestType.PUT, episode.getId(), episode);
        decisionSeason = new DecisionImpl(Sequence.valueOf(3), now, GraphRequestType.PUT, season.getId(), season);
        decisionSeries = new DecisionImpl(Sequence.valueOf(4), now, GraphRequestType.PUT, series.getId(), series);
        
    }
    
    @After
    public void tearDown() {
        graph.clear();

        graph.stop();
    }

    @Test
    public void testGraph() throws GraphException, SQLException, JSONException {
        writeToGraph(false);
        
        testLookingUpById();
        
        testLookingUpBySequence();
        
        testReadRange();
    }

    @Test
    public void testGraphRelatedData() throws GraphException, SQLException {
        start = System.currentTimeMillis();
        graph.write(decisionVideo);
        graph.write(decisionEpisode);
        graph.write(decisionSeason);
        graph.write(decisionSeries);
        System.out.println("Finished writing 4 objects in " + (System.currentTimeMillis() - start)
                + " millis.");

        start = System.currentTimeMillis();
        GraphData graphVideo = graph.readId(VIDEO_ID);
        System.out.println("Finished reading video object graph objects in "
                + (System.currentTimeMillis() - start) + " millis.");

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

        graph.write(decisionVideo);
        graph.write(decisionEpisode);
        graph.write(decisionSeason);
        graph.write(decisionSeries);
        
        GraphData graphEpisode = graph.readId(decisionEpisode.getDataId());
        assertNotNull(graphEpisode);
        graph.delete(graphEpisode);
        
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

        graph.write(decisionVideo);

        GraphData graphVideo = graph.readId(VIDEO_ID);
        assertNotNull(graphVideo);
        assertSame(video, graphVideo.getData());

        graph.delete(graphVideo);
        assertNull(graph.readId(VIDEO_ID));
    }

    @Test
    public void testDeleteTopReference() throws GraphException, SQLException, InterruptedException {

        graph.write(decisionVideo);
        graph.write(decisionEpisode);
        graph.write(decisionSeason);
        graph.write(decisionSeries);

        graph.delete(graph.readId(TVEPISODE_ID));
        graph.delete(graph.readId(TVSERIES_ID));

        assertNull(graph.readId(TVEPISODE_ID));
        assertNull(graph.readId(TVSERIES_ID));

        GraphData graphSeason = graph.readId(TVSEASON_ID);
        assertNull(graphSeason.getRelatedData().getLinks());
        assertNull(graphSeason.getRelatedData().getReferences());
    }

    @Test
    public void testGraphController() throws JSONException, GraphException, InterruptedException,
            SQLException {
        writeToGraph(true);
        assertEquals(CAPACITY, memoryManager.blocks()[0].occupied());
    }
    
    @Test
    public void testClearAllGraphData() throws JSONException, GraphException, InterruptedException,
            SQLException {
        testGraphController();
        
        graph.clear();
        
        assertEquals(0, decisionMaker.latestDecision().number());
        assertEquals(0, memoryManager.occupied());
        assertEquals(CAPACITY, memoryManager.capacity());
        assertEquals(CAPACITY, memoryManager.available());
    }

    private void testLookingUpById() {

        start = System.currentTimeMillis();
        for (int i = 0; i < CAPACITY; i++) {
            assertNotNull(graph.readId(String.valueOf(i)));
            assertEquals(String.valueOf(i), graph.readId(String.valueOf(i)).getData().getId());
        }
        System.out.println("Looking up items by id took: " + (System.currentTimeMillis() - start)
                + " millis");
    }

    private void testLookingUpBySequence() {
        start = System.currentTimeMillis();
        for (int i = 0; i < CAPACITY; i++) {

            assertNotNull(graph.readSequence(Sequence.valueOf(i)) == null);
        }
        System.out.println("Looking up items by sequence id took: "
                + (System.currentTimeMillis() - start) + " millis");
    }

    private void testReadRange() {
        MemoryReference[] chunk = new MemoryReference[CAPACITY];
        for (int i = 0; i < CAPACITY; i++) {
            chunk[i] = MemoryReference.valueOf(i);
        }
        start = System.currentTimeMillis();
        GraphData[] data = graph.readReferences(chunk);
        assertEquals(CAPACITY, data.length);
        for (int i = 0; i < data.length; i++) {
            assertNotNull(data[i]);
            assertEquals(String.valueOf(i), data[i].getData().getId());
        }
        System.out.println("Reading " + CAPACITY + " id(s) took: "
                + (System.currentTimeMillis() - start) + " millis");
    }

    private void initializeGraph() throws SQLException {
        final MemoryBlock block = new MemoryBlockImpl("global", CAPACITY);
        final MemoryBlock[] blocks = new MemoryBlock[] { block };
        final MemoryBlockResolver resolver = new MemoryBlockResolver() {

            @Override
            public MemoryBlock resolve(Data data) {
                return block;
            }

            @Override
            public MemoryBlock[] blocks() {
                return blocks;
            }
        };

        memoryManager = new MemoryManagerImpl(resolver);
        HSQLBookKeeper bookKeeper = new HSQLBookKeeper(DB_NAME, DB_PATH);
        decisionMaker = new SingleNodeDecisionMaker(bookKeeper);
        graph = new GraphImpl(memoryManager);
        graphController = new GraphControllerImpl(graph, decisionMaker);

        // register listeners for graph lifecycle events
        graph.register(bookKeeper);
        graph.register(decisionMaker);
        graph.register(graphController);
        graph.register(MemoryReference.valueOf(0));
        graph.register(Sequence.valueOf(0));
    }

    private void writeToGraph(boolean useController)
            throws GraphException, JSONException {
        start = System.currentTimeMillis();
        final DateTime time = new DateTime();

        for (int i = 0; i < CAPACITY; i++) {
            Data data = new DataImpl(String.valueOf(i), new DateTime(), new DateTime());
            Decision decision =
                    new DecisionImpl(Sequence.valueOf(i), time, GraphRequestType.PUT, data.getId() + i, data);
            
            if(useController) {
                graphController.write(data);
            }
            else {
                graph.write(decision);
            }
        }
    }
}
