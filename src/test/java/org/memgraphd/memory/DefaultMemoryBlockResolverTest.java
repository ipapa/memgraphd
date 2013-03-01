package org.memgraphd.memory;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DefaultMemoryBlockResolverTest {
    
    private DefaultMemoryBlockResolver resolver;
    
    @Before
    public void setUp() throws Exception {
        resolver = new DefaultMemoryBlockResolver(100);
    }

    @Test
    public void testDefaultMemoryBlockResolver() {
        assertNotNull(resolver);
    }

    @Test
    public void testResolve() {
        assertNotNull(resolver.resolve(null));
    }

    @Test
    public void testBlocks() {
        MemoryBlock[] blocks = resolver.blocks();
        assertNotNull(blocks);
        assertEquals(1, blocks.length);
    }

}
