package org.memgraphd.data.library;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.memgraphd.data.Data;
import org.memgraphd.data.GraphData;
import org.memgraphd.data.library.collection.DefaultLibraryDataCollection;
import org.memgraphd.data.library.collection.LibraryDataCollection;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultLibraryTest {
    
    private Library library;
    private Librarian librarian;
    
    private Category category;
    private Category[] categories;
    @Mock private DataPredicate predicate;
    
    private LibrarySection section;
    private LibrarySection[] sections;
    
    @Mock private GraphData gData, gData2;
    @Mock private Data data, data2;
    
    @Before
    public void setUp() throws Exception {
        category = new CategoryImpl("my category", predicate);
        categories = new Category[] { category };
        
        section = new LibrarySectionImpl("some section", categories);
        sections = new LibrarySection[] { section };
        
        DefaultLibrary defaultLib = new DefaultLibrary(sections);
        library = defaultLib;
        librarian = defaultLib;
    }

    @Test
    public void testDefaultLibrary() {
        assertNotNull(library);
    }

    @Test
    public void testGetAvailableSections() {
        assertSame(sections, library.getAvailableSections());
    }
    
    @Test
    public void testgetSection() {
        testArchive();
        LibraryDataCollection coll = library.getSection(section);
        assertNotNull(coll);
        assertEquals(1, coll.size());
        assertSame(gData, coll.list()[0]);
    }
    
    @Test
    public void testgetSection_Null() {
        assertSame(DefaultLibraryDataCollection.EMPTY, library.getSection(null));
    }
    
    @Test
    public void testgetSection_Empty() {
        assertSame(DefaultLibraryDataCollection.EMPTY, library.getSection(section));
    }

    @Test
    public void testSize() {
        assertEquals(0, library.size());
    }

    @Test
    public void testArchive() {
        when(predicate.apply(gData)).thenReturn(true);
        when(gData.getData()).thenReturn(data);
        when(data.getId()).thenReturn("sk2e2");
        
        librarian.archive(gData);
        assertEquals(1, library.size());
     
        verify(predicate).apply(gData);
        verify(gData).getData();
        verify(data).getId();
    }
    
    @Test
    public void testArchive_PredicateFalse() {
        when(predicate.apply(gData)).thenReturn(false);
        librarian.archive(gData);
        assertEquals(0, library.size());
        verify(predicate).apply(gData);
    }
    
    @Test
    public void testArchive_categoryNotEmpty() {
        // archive one item first
        testArchive();
        // archive second item
        when(predicate.apply(gData2)).thenReturn(true);
        when(gData2.getData()).thenReturn(data2);
        when(data2.getId()).thenReturn("sk2edd2");
        
        librarian.archive(gData2);
        assertEquals(2, library.size());
     
        verify(predicate).apply(gData2);
        verify(gData2).getData();
        verify(data2).getId();
    }

    @Test
    public void testUnarchive() {
        
        testArchive();
       
        librarian.unarchive(gData);
       
        assertEquals(0, library.size());
       
    }
    
    @Test
    public void testUnarchive_doesntExist() {
        
        testArchive();
       
        librarian.unarchive(gData2);
       
        assertEquals(1, library.size());
       
    }

}
