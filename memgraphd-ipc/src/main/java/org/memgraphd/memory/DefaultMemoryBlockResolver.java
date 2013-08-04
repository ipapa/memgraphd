package org.memgraphd.memory;

import org.memgraphd.data.Data;
/**
 * Default implementation of {@link MemoryBlockResolver} that creates only one big block
 * called {@link DefaultMemoryBlockResolver#DEFAULT_MEMORYBLOCK_NAME}.
 *
 * @author Ilirjan Papa
 * @since November 5, 2012
 *
 */
public class DefaultMemoryBlockResolver implements MemoryBlockResolver {

    public static final String DEFAULT_MEMORYBLOCK_NAME = "global";

    final MemoryBlock block;
    final MemoryBlock[] blocks;

    public DefaultMemoryBlockResolver(int capacity) {
        this.block = new MemoryBlockImpl(DEFAULT_MEMORYBLOCK_NAME, MemoryReference.valueOf(0), MemoryReference.valueOf(capacity - 1));
        this.blocks = new MemoryBlock[] { block };
    }

    @Override
    public MemoryBlock resolve(Data data) {
        return block;
    }

    @Override
    public MemoryBlock[] blocks() {
        return blocks;
    }

}
