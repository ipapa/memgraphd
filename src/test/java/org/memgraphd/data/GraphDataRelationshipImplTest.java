package org.memgraphd.data;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.memgraphd.test.data.OnlineVideo;
import org.memgraphd.test.data.TvEpisode;
import org.memgraphd.test.data.TvSeason;
import org.memgraphd.test.data.TvSeries;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GraphDataRelationshipImplTest {
    private OnlineVideo video1;
    private OnlineVideo video2;
    private TvEpisode episode;
    private TvSeason season;
    private GraphData[] graphData;
    
    @Mock
    private GraphData video1Graph, video2Graph, episodeGraph, seasonGraph;
    
    private GraphDataRelationshipImpl impl;
    
    @Before
    public void setUp() {
        video1 = new OnlineVideo("1", null, null, null, null);
        video2 = new OnlineVideo("2", null, null, null, null);
        episode = new TvEpisode(null, null, null, null, null, null, null);
        season = new TvSeason(null, null, null, null, null);
        
        when(video1Graph.getData()).thenReturn(video1);
        when(video2Graph.getData()).thenReturn(video2);
        when(episodeGraph.getData()).thenReturn(episode);
        when(seasonGraph.getData()).thenReturn(season);
        
        graphData = new GraphData[] {
                video1Graph, episodeGraph, seasonGraph, video2Graph};
        impl = new GraphDataRelationshipImpl(graphData);
    }
    
    @Test
    public void testGraphDataRelationshipImpl() {
        assertNotNull(impl);
    }

    @Test
    public void testOneToMany() {
        Set<? extends OnlineVideo> newVideos = impl.oneToMany(video1.getClass());
        assertEquals(2, newVideos.size());
        int count = 0;
        for(OnlineVideo v : newVideos) {
            if(count == 0) {
                assertSame(video1, v);
            }
            else {
                assertSame(video2, v);
            }
            count++;
        }
    }
    
    @Test
    public void testOneToMany_doesNotExist() {
        assertTrue(impl.oneToMany(TvSeries.class).isEmpty());
    }

    @Test
    public void testOneToOne() {
        assertSame(video1, impl.oneToOne(video1.getClass()));
        assertSame(episode, impl.oneToOne(episode.getClass()));
        assertSame(season, impl.oneToOne(season.getClass()));
    }
    
    @Test
    public void testOneToOne_doesNotExist() {
        assertNull(impl.oneToOne(TvSeries.class));
    }

    @Test
    public void testRelationships() {
        assertSame(graphData, impl.relationships());
    }

}
