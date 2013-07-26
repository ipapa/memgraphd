package org.memgraphd.data.library;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

@RunWith(MockitoJUnitRunner.class)
public class CategoryImplTest {
    
    private Category category;
    
    @Mock
    private DataPredicate predicate;
    
    @Before
    public void setUp() throws Exception {
        category = new CategoryImpl("my cat name", predicate);
    }

    @Test
    public void testCategoryImpl() {
        assertNotNull(category);
    }

    @Test
    public void testGetName() {
        assertEquals("my cat name", category.getName());
    }

    @Test
    public void testGetPredicate() {
        assertSame(predicate, category.getPredicate());
    }

}
