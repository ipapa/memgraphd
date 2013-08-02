package org.memgraphd.data;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AbstractDataTest {
    private static final String ID = "id";
    private static final DateTime CREATED_DATE = new DateTime();
    private static final DateTime LAST_MODIFIED_DATE = new DateTime();
    
    private ReadWriteData data;
    
    @Before
    public void setUp() {
        data = new ReadWriteData(ID, CREATED_DATE, LAST_MODIFIED_DATE);
    }
    
    @Test
    public void testData() {
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
