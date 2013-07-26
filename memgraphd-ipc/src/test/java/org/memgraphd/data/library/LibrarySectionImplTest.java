package org.memgraphd.data.library;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class LibrarySectionImplTest {
    
    private LibrarySection section;
    
    private Category[] categories;
    
    @Mock
    private Category cat1, cat2, cat3;
    
    @Before
    public void setUp() throws Exception {
        categories = new Category[] { cat1, cat2, cat3 };
        section = new LibrarySectionImpl("my section", categories);
    }

    @Test
    public void testLibrarySectionImpl() {
        assertNotNull(section);
    }
    
    @Test
    public void testLibrarySectionImpl_Null() {
        section = new LibrarySectionImpl("my section", null);
        assertEquals("my section", section.getName());
        assertNotNull(section.getCategories());
    }

    @Test
    public void testGetName() {
        assertEquals("my section", section.getName());
    }

    @Test
    public void testGetCategories() {
        assertEquals(categories.length, section.getCategories().size());
    }

}
