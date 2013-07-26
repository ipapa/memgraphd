package org.memgraphd.data;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class GraphRelatedDataImplTest {
    private GraphRelatedDataImpl data;
    
    @Before
    public void setUp() throws Exception {
        data = new GraphRelatedDataImpl();
    }

    @Test
    public void testGraphRelatedDataImpl() {
        assertNotNull(data);
    }

    @Test
    public void testGetLinks() {
        assertNull(data.getLinks());
    }

    @Test
    public void testGetReferences() {
        assertNull(data.getReferences());
    }

    @Test
    public void testSetRelationships() {
        GraphDataRelationship d = new GraphDataRelationshipImpl(null);
        data.setRelationships(d);
        assertSame(d, data.getLinks());
    }

    @Test
    public void testSetReferences() {
        GraphDataRelationship d = new GraphDataRelationshipImpl(null);
        data.setReferences(d);
        assertSame(d, data.getReferences());
    }

}
