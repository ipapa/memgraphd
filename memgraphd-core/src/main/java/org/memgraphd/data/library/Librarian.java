package org.memgraphd.data.library;

import org.memgraphd.data.GraphData;

public interface Librarian {
    
    void archive(GraphData gData);
    
    void unarchive(GraphData gData);
    
}
