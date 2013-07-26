package org.memgraphd.data;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AbstractDataPermissionsTest {
    
    private ReadOnlyData readOnly;
    private ReadOnlyExpiringData readOnlyExpiringData;
    private ReadOnlyProtectedData readOnlyProtected;
    private ReadWriteData readWriteData;
    private ReadWriteExpiringData readWriteExpiringData;
    private ReadWriteProtectedData readWriteProtectedData;
    
    @Before
    public void setUp() throws Exception {
        readOnly = new ReadOnlyData("id", DateTime.now());
        readOnlyExpiringData = new ReadOnlyExpiringData("id", DateTime.now(), DateTime.now());
        readOnlyProtected = new ReadOnlyProtectedData("id", DateTime.now());
        readWriteData = new ReadWriteData("id", DateTime.now(), null);
        readWriteExpiringData = new ReadWriteExpiringData("id", DateTime.now(), DateTime.now());
        readWriteProtectedData = new ReadWriteProtectedData("id", DateTime.now(), null);
    }
    
    @Test
    public void testCanRead() {
        assertTrue(readOnly.canRead());
        assertTrue(readOnlyExpiringData.canRead());
        assertTrue(readOnlyProtected.canRead());
        assertTrue(readWriteData.canRead());
        assertTrue(readWriteExpiringData.canRead());
        assertTrue(readWriteProtectedData.canRead());
    }

    @Test
    public void testCanWrite() {
        assertTrue(readOnly.canWrite());
        assertTrue(readOnlyExpiringData.canWrite());
        assertTrue(readOnlyProtected.canWrite());
        assertTrue(readWriteData.canWrite());
        assertTrue(readWriteExpiringData.canWrite());
        assertTrue(readWriteProtectedData.canWrite());
    }

    @Test
    public void testCanUpdate() {
        assertFalse(readOnly.canUpdate());
        assertFalse(readOnlyExpiringData.canUpdate());
        assertFalse(readOnlyProtected.canUpdate());
        assertTrue(readWriteData.canUpdate());
        assertTrue(readWriteExpiringData.canUpdate());
        assertTrue(readWriteProtectedData.canUpdate());
    }

    @Test
    public void testCanDelete() {
        assertTrue(readOnly.canDelete());
        assertTrue(readOnlyExpiringData.canDelete());
        assertFalse(readOnlyProtected.canDelete());
        assertTrue(readWriteData.canDelete());
        assertTrue(readWriteExpiringData.canDelete());
        assertFalse(readWriteProtectedData.canDelete());
    }

}
