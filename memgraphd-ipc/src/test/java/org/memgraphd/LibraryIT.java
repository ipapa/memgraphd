package org.memgraphd;

import java.io.File;
import java.sql.SQLException;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.memgraphd.data.GraphData;
import org.memgraphd.data.comparator.SortOrder;
import org.memgraphd.data.library.Category;
import org.memgraphd.data.library.CategoryImpl;
import org.memgraphd.data.library.Library;
import org.memgraphd.data.library.LibrarySection;
import org.memgraphd.data.library.LibrarySectionImpl;
import org.memgraphd.data.library.collection.LibraryDataCollection;
import org.memgraphd.exception.GraphException;
import org.memgraphd.test.data.Movie;
import org.memgraphd.test.data.OnlineVideo;
import org.memgraphd.test.data.TvEpisode;
import org.memgraphd.test.data.TvSeason;
import org.memgraphd.test.data.TvSeries;
import org.memgraphd.test.library.LongFormVideoPredicate;
import org.memgraphd.test.library.MoviePredicate;
import org.memgraphd.test.library.ShortFormVideoPredicate;
import org.memgraphd.test.library.TvSeriesPredicate;
import org.memgraphd.test.library.VideoWithEpisodePredicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LibraryIT {

    private final int CAPACITY = 1000;

    private Graph graph;
    
    private LibrarySection programs, videos;
    
    private OnlineVideo videoLongForm1, videoLongForm2, videoShortForm1, videoShortForm2, videoShortForm3;
    
    private Movie movie1, movie2, movie3;
    
    private TvSeries series1, series2, series3;
    
    private TvSeason season1, season2;
    
    private TvEpisode episode1, episode2, episode3;
    
    private Category movieCategory, seriesCategory, videosWithEpisodesCategory, longFormVideoCategory, shortFormVideoCategory;
    
    private File tmpFile;
    
    @Before
    public void setUp() throws Exception {
        videoLongForm1 = new OnlineVideo("video1", new DateTime(), new DateTime(), "video1", "episode1", true);
        videoLongForm2 = new OnlineVideo("video2", videoLongForm1.getCreatedDate().plus(100L), videoLongForm1.getLastModifiedDate().plus(100L), "video2", "episode2", true);
        videoShortForm1 = new OnlineVideo("video3", videoLongForm2.getCreatedDate().plus(100L), videoLongForm2.getLastModifiedDate().plus(100L), "video3", "episode3", false);
        videoShortForm2 = new OnlineVideo("video4", videoShortForm1.getCreatedDate().plus(100L), videoShortForm1.getLastModifiedDate().plus(100L), "video4", "episode1", false);
        videoShortForm3 = new OnlineVideo("video5", videoShortForm2.getCreatedDate().plus(100L), videoShortForm2.getLastModifiedDate().plus(100L), "video5", "episode1", false);
        
        movie1 = new Movie("movie1", "Movie#1", "network1", null, null);
        movie2 = new Movie("movie2", "Movie#2", "network2", null, null);
        movie3 = new Movie("movie3", "Movie#3", "network3", null, null);
        
        series1 = new TvSeries("series1", "network1", null, null, "Series#1");
        series2 = new TvSeries("series2", "network2", null, null, "Series#2");
        series3 = new TvSeries("series3", "network3", null, null, "Series#3");
        
        season1 = new TvSeason("season1", null, null, "Season#1", "series1");
        season2 = new TvSeason("season2", null, null, "Season#2", "series1");
        
        episode1 = new TvEpisode("episode1", null, null, "season1", "Episode#1", "1", null);
        episode2 = new TvEpisode("episode2", null, null, "season1", "Episode#2", "2", null);
        episode3 = new TvEpisode("episode3", null, null, "season1", "Episode#3", "3", null);
        
        movieCategory = new CategoryImpl("movies", new MoviePredicate());
        seriesCategory = new CategoryImpl("tv series", new TvSeriesPredicate());
        videosWithEpisodesCategory = new CategoryImpl("videos-with-episodes", new VideoWithEpisodePredicate());
        longFormVideoCategory = new CategoryImpl("long-form", new LongFormVideoPredicate());
        shortFormVideoCategory = new CategoryImpl("short-form", new ShortFormVideoPredicate());
        
        programs = new LibrarySectionImpl("program", new Category[] { movieCategory, seriesCategory});
        videos = new LibrarySectionImpl("videos", new Category[] { videosWithEpisodesCategory, longFormVideoCategory, shortFormVideoCategory});
        
        tmpFile = File.createTempFile("lib", "");
        
        graph =  GraphImpl.build(new GraphConfigDefaults("someGraph", CAPACITY, "testlibrary", 
                tmpFile.getAbsolutePath(), 100, 1000, new LibrarySection[] { videos, programs }));
        
        graph.run();
    }
    
    @After
    public void tearDown() throws Exception {
        graph.clear();
        graph.shutdown();
        
        tmpFile.delete();
    }
    
    @Test
    public void testGetLibrary_empty() throws GraphException, SQLException {
        graph = GraphImpl.build(new GraphConfigDefaults());
        graph.run();
        
        Library library = graph.getLibrary();
        
        assertNotNull(library);
        assertNotNull(library.getAvailableSections());
        assertEquals(0, library.getAvailableSections().length);
    }
    
    @Test
    public void testGetLibrary_integrity() throws GraphException, SQLException {
        
        Library library = graph.getLibrary();
        
        assertNotNull(library);
        assertNotNull(library.getAvailableSections());
        assertEquals(2, library.getAvailableSections().length);
        
        LibraryDataCollection programCollection = library.getSection(programs);
        assertNotNull(programCollection);
        assertEquals(0, programCollection.list().length);
        
        LibraryDataCollection videoCollection = library.getSection(videos);
        assertNotNull(videoCollection);
        assertEquals(0, videoCollection.list().length);
    }
    
    
    @Test
    public void testGetLibrary_irelevantDataWritten() throws GraphException, SQLException {
        
        graph.create(episode1);
        graph.create(season1);
        
        Library library = graph.getLibrary();
        
        assertNotNull(library);
        assertEquals(0, library.size());
        assertNotNull(library.getAvailableSections());
        assertEquals(2, library.getAvailableSections().length);
        
        LibraryDataCollection programCollection = library.getSection(programs);
        assertNotNull(programCollection);
        assertEquals(0, programCollection.list().length);
        
        LibraryDataCollection videoCollection = library.getSection(videos);
        assertNotNull(videoCollection);
        assertEquals(0, videoCollection.list().length);
    }
    
    @Test
    public void testGetLibrary() throws GraphException, SQLException {
        
        graph.create(episode1);
        graph.create(videoLongForm1);
        graph.create(videoLongForm2);
        graph.create(movie1);
        graph.create(videoShortForm3);
        graph.create(season2);
        graph.create(series1);
        graph.create(season1);
        graph.create(episode2);
        graph.create(episode3);
        
        Library library = graph.getLibrary();
        
        assertNotNull(library);
        assertNotNull(library.getAvailableSections());
        assertEquals(2, library.getAvailableSections().length);
        
        LibraryDataCollection programCollection = library.getSection(programs);
        assertNotNull(programCollection);
        assertEquals(2, programCollection.list().length);
        
        LibraryDataCollection videoCollection = library.getSection(videos);
        assertNotNull(videoCollection);
        assertEquals(3, videoCollection.list().length);
    }
    
    @Test
    public void testGetLibrary_size() throws GraphException, SQLException {
        
        Library library = graph.getLibrary();
        
        assertEquals(0, library.size());
        
        graph.create(videoLongForm1);
        graph.create(videoLongForm2);
        graph.create(videoShortForm1);
        graph.create(videoShortForm2);
        graph.create(videoShortForm3);
        
        graph.create(movie1);
        graph.create(movie2); 
        graph.create(movie3);
        graph.create(series1);
        graph.create(series2);
        graph.create(series3);
        
        assertEquals(11, library.size());
 
        assertEquals(11, library.size());
        assertEquals(6, library.getSection(programs).size());
        assertEquals(5, library.getSection(videos).size());
    }
    
    @Test
    public void testGetLibrary_sortById() throws GraphException, SQLException {

        graph.create(videoLongForm1);
        graph.create(videoLongForm2);
        graph.create(videoShortForm1);
        graph.create(videoShortForm2);
        graph.create(videoShortForm3);
        
        Library library = graph.getLibrary();
        LibraryDataCollection videoColl = library.getSection(videos);
        
        GraphData[] listSortedDesc = videoColl.sortById(SortOrder.ASCENDING).list();
        assertEquals(videoLongForm1.getId(), listSortedDesc[0].getData().getId());
        assertEquals(videoLongForm2.getId(), listSortedDesc[1].getData().getId());
        assertEquals(videoShortForm1.getId(), listSortedDesc[2].getData().getId());
        assertEquals(videoShortForm2.getId(), listSortedDesc[3].getData().getId());
        assertEquals(videoShortForm3.getId(), listSortedDesc[4].getData().getId());
        
        GraphData[] listSortedAsc = videoColl.sortById(SortOrder.DESCENDING).list();
        assertEquals(videoShortForm3.getId(), listSortedAsc[0].getData().getId());
        assertEquals(videoShortForm2.getId(), listSortedAsc[1].getData().getId());
        assertEquals(videoShortForm1.getId(), listSortedAsc[2].getData().getId());
        assertEquals(videoLongForm2.getId(), listSortedAsc[3].getData().getId());
        assertEquals(videoLongForm1.getId(), listSortedAsc[4].getData().getId());
    }
    
    @Test
    public void testGetLibrary_sortByCreatedDate() throws GraphException, SQLException {

        graph.create(videoLongForm1);
        graph.create(videoLongForm2);
        graph.create(videoShortForm1);
        graph.create(videoShortForm2);
        graph.create(videoShortForm3);
        
        Library library = graph.getLibrary();
        LibraryDataCollection videoColl = library.getSection(videos);
        
        GraphData[] listSortedDesc = videoColl.sortByCreatedDate(SortOrder.ASCENDING).list();
        assertEquals(videoLongForm1.getId(), listSortedDesc[0].getData().getId());
        assertEquals(videoLongForm2.getId(), listSortedDesc[1].getData().getId());
        assertEquals(videoShortForm1.getId(), listSortedDesc[2].getData().getId());
        assertEquals(videoShortForm2.getId(), listSortedDesc[3].getData().getId());
        assertEquals(videoShortForm3.getId(), listSortedDesc[4].getData().getId());
        
        GraphData[] listSortedAsc = videoColl.sortByCreatedDate(SortOrder.DESCENDING).list();
        assertEquals(videoShortForm3.getId(), listSortedAsc[0].getData().getId());
        assertEquals(videoShortForm2.getId(), listSortedAsc[1].getData().getId());
        assertEquals(videoShortForm1.getId(), listSortedAsc[2].getData().getId());
        assertEquals(videoLongForm2.getId(), listSortedAsc[3].getData().getId());
        assertEquals(videoLongForm1.getId(), listSortedAsc[4].getData().getId());
    }
    
    @Test
    public void testGetLibrary_sortByLastModifiedDate() throws GraphException, SQLException {

        graph.create(videoLongForm1);
        graph.create(videoLongForm2);
        graph.create(videoShortForm1);
        graph.create(videoShortForm2);
        graph.create(videoShortForm3);
        
        Library library = graph.getLibrary();
        LibraryDataCollection videoColl = library.getSection(videos);
        
        GraphData[] listSortedDesc = videoColl.sortByLastModifiedDate(SortOrder.ASCENDING).list();
        assertEquals(videoLongForm1.getId(), listSortedDesc[0].getData().getId());
        assertEquals(videoLongForm2.getId(), listSortedDesc[1].getData().getId());
        assertEquals(videoShortForm1.getId(), listSortedDesc[2].getData().getId());
        assertEquals(videoShortForm2.getId(), listSortedDesc[3].getData().getId());
        assertEquals(videoShortForm3.getId(), listSortedDesc[4].getData().getId());
        
        GraphData[] listSortedAsc = videoColl.sortByLastModifiedDate(SortOrder.DESCENDING).list();
        assertEquals(videoShortForm3.getId(), listSortedAsc[0].getData().getId());
        assertEquals(videoShortForm2.getId(), listSortedAsc[1].getData().getId());
        assertEquals(videoShortForm1.getId(), listSortedAsc[2].getData().getId());
        assertEquals(videoLongForm2.getId(), listSortedAsc[3].getData().getId());
        assertEquals(videoLongForm1.getId(), listSortedAsc[4].getData().getId());
    }
    
    @Test
    public void testGetLibrary_filterBy() throws GraphException, SQLException {

        graph.create(videoLongForm1);
        graph.create(videoLongForm2);
        graph.create(videoShortForm1);
        graph.create(videoShortForm2);
        graph.create(videoShortForm3);
        
        Library library = graph.getLibrary();
        LibraryDataCollection videoColl = library.getSection(videos);
        
        GraphData[] dataSet = videoColl.filterBy().list();
        
        assertNotNull(dataSet);
        assertEquals(5, dataSet.length);
    }
    
    @Test
    public void testGetLibrary_filterByCategory() throws GraphException, SQLException {

        graph.create(videoLongForm1);
        graph.create(videoLongForm2);
        graph.create(videoShortForm1);
        graph.create(videoShortForm2);
        graph.create(videoShortForm3);
        
        Library library = graph.getLibrary();
        LibraryDataCollection videoColl = library.getSection(videos);
        
        LibraryDataCollection longFormVideosColl = videoColl.filterBy(longFormVideoCategory);
        LibraryDataCollection shortFormVideosColl = videoColl.filterBy(shortFormVideoCategory);
        LibraryDataCollection movieColl = videoColl.filterBy(movieCategory);
        
        assertNotNull(longFormVideosColl);
        assertNotNull(shortFormVideosColl);
        assertNotNull(movieColl);
        
        assertEquals(2, longFormVideosColl.size());
        assertEquals(3, shortFormVideosColl.size());
        assertEquals(0, movieColl.size());
    }
    
    @Test
    public void testGetLibrary_filterByAll() throws GraphException, SQLException {

        graph.create(videoLongForm1);
        graph.create(videoLongForm2);
        graph.create(videoShortForm1);
        graph.create(videoShortForm2);
        graph.create(videoShortForm3);
        
        Library library = graph.getLibrary();
        LibraryDataCollection videoColl = library.getSection(videos);
        
        LibraryDataCollection longFormColl = videoColl.filterByAll(
                new Category[] { videosWithEpisodesCategory, longFormVideoCategory });
        
        assertNotNull(longFormColl);
        assertEquals(1, longFormColl.size());
        
        LibraryDataCollection shortFormColl = videoColl.filterByAll(
                new Category[] { videosWithEpisodesCategory, shortFormVideoCategory });
        
        assertNotNull(shortFormColl);
        assertEquals(2, shortFormColl.size());
        
    }
    
    @Test
    public void testGetLibrary_filterByAny() throws GraphException, SQLException {

        graph.create(videoLongForm1);
        graph.create(videoLongForm2);
        graph.create(videoShortForm1);
        graph.create(videoShortForm2);
        graph.create(videoShortForm3);
        
        Library library = graph.getLibrary();
        LibraryDataCollection videoColl = library.getSection(videos);
        
        LibraryDataCollection longFormColl = videoColl.filterByAny(
                new Category[] { videosWithEpisodesCategory, longFormVideoCategory });
        
        assertNotNull(longFormColl);
        assertEquals(4, longFormColl.size());
        
        LibraryDataCollection shortFormColl = videoColl.filterByAny(
                new Category[] { videosWithEpisodesCategory, shortFormVideoCategory });
        
        assertNotNull(shortFormColl);
        assertEquals(4, shortFormColl.size());
        
    }
    
    @Test
    public void testGetLibrary_filterByDataPredicate() throws GraphException, SQLException {

        graph.create(videoLongForm1);
        graph.create(videoLongForm2);
        graph.create(videoShortForm1);
        graph.create(videoShortForm2);
        graph.create(videoShortForm3);
        
        Library library = graph.getLibrary();
        LibraryDataCollection videoColl = library.getSection(videos);
        
        LibraryDataCollection longFormColl = videoColl.filterBy(new LongFormVideoPredicate());
        
        assertNotNull(longFormColl);
        assertEquals(2, longFormColl.size());
        
        LibraryDataCollection shortFormColl = videoColl.filterBy(new ShortFormVideoPredicate());
        
        assertNotNull(shortFormColl);
        assertEquals(3, shortFormColl.size());
        
    }
    
    @Test
    public void testGetLibrary_combineFilterByWithSortBy() throws GraphException, SQLException {

        graph.create(videoLongForm1);
        graph.create(videoLongForm2);
        graph.create(videoShortForm1);
        graph.create(videoShortForm2);
        graph.create(videoShortForm3);
        
        Library library = graph.getLibrary();
        LibraryDataCollection videoColl = library.getSection(videos);
        
        LibraryDataCollection longFormColl = videoColl.filterBy(new LongFormVideoPredicate()).sortByCreatedDate(SortOrder.ASCENDING);
        
        assertNotNull(longFormColl);
        assertEquals(2, longFormColl.size());
        assertEquals(videoLongForm1.getId(), longFormColl.list()[0].getData().getId());
        assertEquals(videoLongForm2.getId(), longFormColl.list()[1].getData().getId());
        
        longFormColl = videoColl.sortByCreatedDate(SortOrder.ASCENDING).filterBy(new LongFormVideoPredicate());
        
        assertNotNull(longFormColl);
        assertEquals(2, longFormColl.size());
        assertEquals(videoLongForm1.getId(), longFormColl.list()[0].getData().getId());
        assertEquals(videoLongForm2.getId(), longFormColl.list()[1].getData().getId());
        
    }
    
    @Test
    public void testGetLibrary_combineAllFilterBy() throws GraphException, SQLException {

        graph.create(videoLongForm1);
        graph.create(videoLongForm2);
        graph.create(videoShortForm1);
        graph.create(videoShortForm2);
        graph.create(videoShortForm3);
        
        Library library = graph.getLibrary();
        LibraryDataCollection videoColl = library.getSection(videos);
        
        LibraryDataCollection longFormColl = videoColl.filterBy(new LongFormVideoPredicate()).
                filterBy(videosWithEpisodesCategory).filterBy();
        
        assertNotNull(longFormColl);
        assertEquals(1, longFormColl.size());
        assertEquals(videoLongForm1.getId(), longFormColl.list()[0].getData().getId());
    }
}
