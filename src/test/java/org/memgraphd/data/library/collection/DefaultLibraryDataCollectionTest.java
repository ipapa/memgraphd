package org.memgraphd.data.library.collection;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.memgraphd.data.GraphData;
import org.memgraphd.data.comparator.SortOrder;
import org.memgraphd.data.library.Category;
import org.memgraphd.data.library.DataPredicate;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultLibraryDataCollectionTest {
    
    private DefaultLibraryDataCollection collection;
    
    @Mock
    private SortableDataCollection sortable;
    
    @Mock
    private FilterableDataCollection filterable;
    
    @Mock
    private GraphData gData;
    
    @Mock
    private Category cat1, cat2;
    
    @Mock
    private DataPredicate pred;
    
    @Mock
    private List<GraphData> list;
    
    @Before
    public void setUp() throws Exception {
        collection = new DefaultLibraryDataCollection(list);
        ReflectionTestUtils.setField(collection, "sortable", sortable);
        ReflectionTestUtils.setField(collection, "filterable", filterable);
    }

    @Test
    public void testDefaultLibraryDataCollection() {
        assertNotNull(collection);
    }

    @Test
    public void testSortById() {
        when(sortable.sortById(SortOrder.ASCENDING)).thenReturn(null);
        assertNull(collection.sortById(SortOrder.ASCENDING));
        verify(sortable).sortById(SortOrder.ASCENDING);
    }

    @Test
    public void testSortByCreatedDate() {
        when(sortable.sortByCreatedDate(SortOrder.ASCENDING)).thenReturn(null);
        assertNull(collection.sortByCreatedDate(SortOrder.ASCENDING));
        verify(sortable).sortByCreatedDate(SortOrder.ASCENDING);
    }

    @Test
    public void testSortByLastModifiedDate() {
        when(sortable.sortByLastModifiedDate(SortOrder.ASCENDING)).thenReturn(null);
        assertNull(collection.sortByLastModifiedDate(SortOrder.ASCENDING));
        verify(sortable).sortByLastModifiedDate(SortOrder.ASCENDING);
    }

    @Test
    public void testFilterBy() {
        when(filterable.filterBy()).thenReturn(null);
        assertNull(collection.filterBy());
        verify(filterable).filterBy();
    }

    @Test
    public void testFilterByCategory() {
        when(filterable.filterBy(cat1)).thenReturn(null);
        assertNull(collection.filterBy(cat1));
        verify(filterable).filterBy(cat1);
    }

    @Test
    public void testFilterByAll() {
        Category[] categories = new Category[] { cat1, cat2 };
        when(filterable.filterByAll(categories)).thenReturn(null);
        assertNull(collection.filterByAll(new Category[] { cat1, cat2 }));
        verify(filterable).filterByAll(categories);
    }

    @Test
    public void testFilterByAny() {
        Category[] categories = new Category[] { cat1, cat2 };
        when(filterable.filterByAny(categories)).thenReturn(null);
        assertNull(collection.filterByAny(new Category[] { cat1, cat2 }));
        verify(filterable).filterByAny(categories);
    }

    @Test
    public void testFilterByDataPredicate() {
        when(filterable.filterBy(pred)).thenReturn(null);
        assertNull(collection.filterBy(pred));
        verify(filterable).filterBy(pred);
    }

    @Test
    public void testSize() {
        when(list.size()).thenReturn(1);
        assertEquals(1, collection.size());
        verify(list).size();
    }

    @Test
    public void testContains() {
        when(list.contains(gData)).thenReturn(true);
        assertTrue(collection.contains(gData));
        verify(list).contains(gData);
    }

    @Test
    public void testList() {
        when(list.toArray(new GraphData[] {})).thenReturn(null);
        assertNull(collection.list());
        verify(list).toArray(new GraphData[] {});
    }

}
