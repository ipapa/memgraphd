package org.memgraphd.data.library.collection;

import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.memgraphd.data.Data;
import org.memgraphd.data.GraphData;
import org.memgraphd.data.comparator.SortOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultSortableDataCollectionTest {
    
    private DefaultSortableDataCollection collection;
    
    private List<GraphData> list;
    
    @Mock
    private GraphData gData1, gData2, gData3;
    
    @Mock
    private Data data1, data2, data3;
    
    @Before
    public void setUp() throws Exception {
        mockGraphData();
        mockData();
        list = Arrays.asList(gData1, gData2, gData3);
        collection = new DefaultSortableDataCollection(list);
    }
    
    private void mockGraphData() {
        when(gData1.getData()).thenReturn(data1);
        when(gData2.getData()).thenReturn(data2);
        when(gData3.getData()).thenReturn(data3);
    }
    
    private void mockData() {
        when(data1.getId()).thenReturn("A");
        when(data2.getId()).thenReturn("B");
        when(data3.getId()).thenReturn("C");
        
        DateTime time = new DateTime();
        when(data1.getCreatedDate()).thenReturn(time);
        when(data2.getCreatedDate()).thenReturn(time.plus(1));
        when(data3.getCreatedDate()).thenReturn(time.plus(2));
        
        when(data1.getLastModifiedDate()).thenReturn(time);
        when(data2.getLastModifiedDate()).thenReturn(time.plus(1));
        when(data3.getLastModifiedDate()).thenReturn(time.plus(2));
    }
    
    @Test
    public void testDefaultSortableDataCollection() {
        assertNotNull(collection);
    }

    @Test
    public void testSortById_Ascending() {
        LibraryDataCollection coll = collection.sortById(SortOrder.ASCENDING);
        assertNotNull(coll);
        assertEquals(3, coll.size());
        assertSame(gData1, coll.list()[0]);
        assertSame(gData2, coll.list()[1]);
        assertSame(gData3, coll.list()[2]);
    }
    
    @Test
    public void testSortById_Descending() {
        LibraryDataCollection coll = collection.sortById(SortOrder.DESCENDING);
        assertNotNull(coll);
        assertEquals(3, coll.size());
        assertSame(gData3, coll.list()[0]);
        assertSame(gData2, coll.list()[1]);
        assertSame(gData1, coll.list()[2]);
    }
    
    @Test
    public void testSortByCreatedDate_Ascending() {
        LibraryDataCollection coll = collection.sortByCreatedDate(SortOrder.ASCENDING);
        assertNotNull(coll);
        assertEquals(3, coll.size());
        assertSame(gData1, coll.list()[0]);
        assertSame(gData2, coll.list()[1]);
        assertSame(gData3, coll.list()[2]);
    }
    
    @Test
    public void testSortByCreatedDate_Descending() {
        LibraryDataCollection coll = collection.sortByCreatedDate(SortOrder.DESCENDING);
        assertNotNull(coll);
        assertEquals(3, coll.size());
        assertSame(gData3, coll.list()[0]);
        assertSame(gData2, coll.list()[1]);
        assertSame(gData1, coll.list()[2]);
    }

    @Test
    public void testSortByLastModifiedDate_Ascending() {
        LibraryDataCollection coll = collection.sortByLastModifiedDate(SortOrder.ASCENDING);
        assertNotNull(coll);
        assertEquals(3, coll.size());
        assertSame(gData1, coll.list()[0]);
        assertSame(gData2, coll.list()[1]);
        assertSame(gData3, coll.list()[2]);
    }
    
    @Test
    public void testSortByLastModifiedDate_Descending() {
        LibraryDataCollection coll = collection.sortByLastModifiedDate(SortOrder.DESCENDING);
        assertNotNull(coll);
        assertEquals(3, coll.size());
        assertSame(gData3, coll.list()[0]);
        assertSame(gData2, coll.list()[1]);
        assertSame(gData1, coll.list()[2]);
    }

}
