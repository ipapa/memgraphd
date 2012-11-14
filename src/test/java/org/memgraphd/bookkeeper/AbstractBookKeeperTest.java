package org.memgraphd.bookkeeper;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.memgraphd.data.Data;
import org.memgraphd.data.DataImpl;
import org.memgraphd.decision.Decision;
import org.memgraphd.decision.Sequence;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractBookKeeperTest {
    private AbstractBookKeeper keeper;
    
    @Mock
    private Decision decision;
    
    private Data data;
    
    @Before
    public void setUp() throws Exception {
        keeper =  new HSQLBookKeeper();
        data = new DataImpl("someId", new DateTime(), new DateTime());
        when(decision.getData()).thenReturn(data);
    }

    @Test
    public void testAbstractBookKeeper() {
        assertNotNull(keeper);
    }

    @Test
    public void testOpenBook() {
        assertFalse(keeper.isBookOpen());
        keeper.openBook();
        assertTrue(keeper.isBookOpen());
    }
    
    @Test
    public void testOpenBook_alreadyOpened() {
        testOpenBook();
        keeper.openBook();
        assertTrue(keeper.isBookOpen());
    }

    @Test(expected=RuntimeException.class)
    public void testRecord_bookNotOpened() {
        keeper.record(decision);
    }
    
    @Test
    public void testRecord() {
        keeper.openBook();
        keeper.record(decision);
    }

    @Test
    public void testCloseBook() {
        assertFalse(keeper.isBookOpen());
        keeper.openBook();
        keeper.closeBook();
        assertFalse(keeper.isBookOpen());
    }
    
    @Test
    public void testCloseBook_bookNotOpened() {
        keeper.closeBook();
        assertFalse(keeper.isBookOpen());
    }
    
    @Test
    public void testCloseBook_bookAlreadyClosed() {
        testCloseBook();
        keeper.closeBook();
        assertFalse(keeper.isBookOpen());
    }

    @Test
    public void testIsBookOpen() {
        assertFalse(keeper.isBookOpen());
        keeper.openBook();
        assertTrue(keeper.isBookOpen());
        keeper.closeBook();
        assertFalse(keeper.isBookOpen());
    }
    
    @Test(expected=RuntimeException.class)
    public void testLastTransactionId_bookNotOpened() {
        keeper.lastTransactionId();
    }
    
    @Test
    public void testLastTransactionId() {
        keeper.openBook();
        assertNotNull(keeper.lastTransactionId());
        assertEquals(0, keeper.lastTransactionId().number());
    }

    @Test
    public void testReadRange() {
        keeper.openBook();
        assertTrue(keeper.readRange(Sequence.valueOf(1), Sequence.valueOf(2)).isEmpty());
    }
    
    @Test(expected=RuntimeException.class)
    public void testReadRange_bookNotOpened() {
        keeper.readRange(Sequence.valueOf(1), Sequence.valueOf(2));
    }

}
