package org.memgraphd.data;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.*;

public class AbstractExpiringDataTest {
    private ExpiringData expiringData;
    
    private DateTime expirationDate;
    
    @Before
    public void setUp() throws Exception {
        expirationDate = DateTime.now();
        expiringData = new ReadOnlyExpiringData("id", null, expirationDate);
    }

    @Test
    public void testGetExpirationDate() {
        assertSame(expirationDate, expiringData.getExpirationDate());
    }
    
    @Test
    public void testHasExpired_expirationDateNull() {
        expiringData = new ReadOnlyExpiringData("id", null, null);
        assertFalse(expiringData.hasExpired());
    }
    
    @Test
    public void testHasExpired_before() {
        ReflectionTestUtils.setField(expiringData, "expirationDate", expirationDate.minus(10L));
        assertTrue(expiringData.hasExpired());
    }
    
    @Test
    public void testHasExpired_same() {
        assertFalse(expiringData.hasExpired());
    }
    
    @Test
    public void testHasExpired_after() {
        ReflectionTestUtils.setField(expiringData, "expirationDate", expirationDate.plus(10L));
        assertFalse(expiringData.hasExpired());
    }

}
