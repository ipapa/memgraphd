package org.memgraphd.data;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.memgraphd.GraphRequestType;
import org.memgraphd.decision.DecisionImpl;
import org.memgraphd.decision.Sequence;
import org.memgraphd.memory.MemoryReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class GraphDataImplTest {
    private final MemoryReference REFERENCE = MemoryReference.valueOf(1);
    private final GraphRequestType REQUEST_TYPE = GraphRequestType.CREATE;
    private final String DATA_ID = "data-1";
    private final Data DATA = new ReadWriteData(DATA_ID, null, null);
    private final DateTime TIME = new DateTime();
    private final Sequence SEQUENCE = Sequence.valueOf(1);
    
    private GraphDataImpl data;
    
    @Before
    public void setUp() {
        data = new GraphDataImpl(
                new DecisionImpl(SEQUENCE, TIME, REQUEST_TYPE, DATA_ID, DATA));
    }
    
    @Test
    public void testGraphDataImpl() {
        assertNotNull(data);
    }

    @Test
    public void testGetReference() {
        data.setRefence(REFERENCE);
        assertEquals(REFERENCE, data.getReference());
    }

    @Test
    public void testGetData() {
        assertEquals(DATA, data.getData());
    }

    @Test
    public void testSetRefence() {
        assertNull(data.getReference());
        data.setRefence(REFERENCE);
        assertEquals(REFERENCE, data.getReference());
    }

    @Test
    public void testGetRelatedData() {
        assertNotNull(data.getRelatedData());
        assertNull(data.getRelatedData().getLinks());
        assertNull(data.getRelatedData().getReferences());
    }

    @Test
    public void testGetSequence() {
        assertEquals(SEQUENCE, data.getSequence());
    }

    @Test
    public void testToString() {
        assertNotNull(data.toString());
    }

}
