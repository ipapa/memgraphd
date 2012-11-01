package org.memgraphd.memory;

public enum MemoryState {
    
    FREE(false, true),
    RESERVED(false, true),
    IN_USE(true, true),
    CORRUPTED(false, false);
    
    private final boolean readable;
    private final boolean writable;
    
    private MemoryState(boolean readable, boolean writable) {
        this.readable = readable;
        this.writable = writable;
    }
    
    public boolean isReadable() {
        return readable;
    }
    
    public boolean isWritable() {
        return writable;
    }
}
