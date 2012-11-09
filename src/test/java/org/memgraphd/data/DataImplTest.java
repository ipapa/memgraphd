package org.memgraphd.data;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DataImplTest {
    private static final String ID = "id";
    private static final DateTime CREATED_DATE = new DateTime();
    private static final DateTime LAST_MODIFIED_DATE = new DateTime();
    
    private DataImpl data;
    
    @Before
    public void setUp() {
        data = new DataImpl(ID, CREATED_DATE, LAST_MODIFIED_DATE);
    }
    
    @Test
    public void testDataImpl() {
        assertNotNull(data);
    }

    @Test
    public void testGetId() {
        assertEquals(ID, data.getId());
    }

    @Test
    public void testGetCreatedDate() {
        assertEquals(CREATED_DATE, data.getCreatedDate());
    }

    @Test
    public void testGetLastModifiedDate() {
        assertEquals(LAST_MODIFIED_DATE, data.getLastModifiedDate());
    }

}
