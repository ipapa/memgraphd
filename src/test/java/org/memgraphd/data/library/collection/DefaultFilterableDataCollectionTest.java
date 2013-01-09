package org.memgraphd.data.library.collection;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.memgraphd.data.Data;
import org.memgraphd.data.GraphData;
import org.memgraphd.data.library.Category;
import org.memgraphd.data.library.CategoryImpl;
import org.memgraphd.data.library.DataPredicate;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultFilterableDataCollectionTest {
    private DefaultFilterableDataCollection collection;
    
    private List<GraphData> list;
    
    @Mock
    private GraphData gData1, gData2, gData3;
    
    @Mock
    private Data data1, data2, data3;

    private Category cat1, cat2, cat3;
    
    @Mock
    private DataPredicate pred1, pred2, pred3;
    
    @Before
    public void setUp() throws Exception {
        mockGraphData(gData1, data1, "data 1");
        mockGraphData(gData2, data2, "data 2");
        mockGraphData(gData3, data3, "data 3");
        
        list = Arrays.asList(gData1, gData2, gData3);
        
        collection = new DefaultFilterableDataCollection(list);
        
        cat1 = new CategoryImpl("cat1", pred1);
        cat2 = new CategoryImpl("cat2", pred2);
        cat3 = new CategoryImpl("cat3", pred3);
    }
    
    private void mockGraphData(GraphData gData, Data data, String dataId) {
        when(gData.getData()).thenReturn(data);
        when(data.getId()).thenReturn(dataId);
    }
    
    @Test
    public void testDefaultFilterableDataCollection() {
        assertNotNull(collection);
    }

    @Test
    public void testFilterBy() {
        assertEquals(3, collection.filterBy().size());
    }

    @Test
    public void testFilterByCategory() {
        when(cat1.getPredicate().apply(gData1)).thenReturn(true);
        when(cat1.getPredicate().apply(gData2)).thenReturn(false);
        when(cat1.getPredicate().apply(gData3)).thenReturn(true);
        
        LibraryDataCollection coll = collection.filterBy(cat1);
        
        assertNotNull(coll);
        assertEquals(2, coll.size());
        assertTrue(coll.contains(gData1));
        assertTrue(coll.contains(gData3));
    }

    @Test
    public void testFilterByAll() {
        when(cat1.getPredicate().apply(gData1)).thenReturn(true);
        when(cat1.getPredicate().apply(gData2)).thenReturn(true);
        when(cat1.getPredicate().apply(gData3)).thenReturn(true);
        
        when(cat2.getPredicate().apply(gData1)).thenReturn(true);
        when(cat2.getPredicate().apply(gData2)).thenReturn(false);
        when(cat2.getPredicate().apply(gData3)).thenReturn(false);
        
        LibraryDataCollection coll = collection.filterByAll(new Category[] { cat1, cat2 });
        
        assertNotNull(coll);
        assertEquals(1, coll.size());
        assertTrue(coll.contains(gData1));
    }

    @Test
    public void testFilterByAny() {
        when(cat1.getPredicate().apply(gData1)).thenReturn(true);
        when(cat1.getPredicate().apply(gData2)).thenReturn(false);
        when(cat1.getPredicate().apply(gData3)).thenReturn(false);
        
        when(cat2.getPredicate().apply(gData1)).thenReturn(false);
        when(cat2.getPredicate().apply(gData2)).thenReturn(true);
        when(cat2.getPredicate().apply(gData3)).thenReturn(false);
        
        when(cat3.getPredicate().apply(gData1)).thenReturn(false);
        when(cat3.getPredicate().apply(gData2)).thenReturn(false);
        when(cat3.getPredicate().apply(gData3)).thenReturn(true);
        
        LibraryDataCollection coll = collection.filterByAny(new Category[] { cat1, cat2, cat3 });
        
        assertNotNull(coll);
        assertEquals(3, coll.size());
        assertTrue(coll.contains(gData1));
        assertTrue(coll.contains(gData2));
        assertTrue(coll.contains(gData3));
    }
    
    @Test
    public void testFilterByAny_NoMatch() {
        when(cat1.getPredicate().apply(gData1)).thenReturn(false);
        when(cat1.getPredicate().apply(gData2)).thenReturn(false);
        when(cat1.getPredicate().apply(gData3)).thenReturn(false);
        
        when(cat2.getPredicate().apply(gData1)).thenReturn(false);
        when(cat2.getPredicate().apply(gData2)).thenReturn(false);
        when(cat2.getPredicate().apply(gData3)).thenReturn(false);
        
        when(cat3.getPredicate().apply(gData1)).thenReturn(false);
        when(cat3.getPredicate().apply(gData2)).thenReturn(false);
        when(cat3.getPredicate().apply(gData3)).thenReturn(false);
        
        LibraryDataCollection coll = collection.filterByAny(new Category[] { cat1, cat2, cat3 });
        
        assertNotNull(coll);
        assertEquals(0, coll.size());
    }

    @Test
    public void testFilterByDataPredicate() {
        when(pred1.apply(gData1)).thenReturn(false);
        when(pred1.apply(gData2)).thenReturn(true);
        when(pred1.apply(gData3)).thenReturn(false);
        
        LibraryDataCollection coll = collection.filterBy(pred1);
        
        assertNotNull(coll);
        assertEquals(1, coll.size());
        assertTrue(coll.contains(gData2));
    }

}
