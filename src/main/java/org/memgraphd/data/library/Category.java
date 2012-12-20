package org.memgraphd.data.library;

public interface Category {
    
    String getName();
    
    DataPredicate getPredicate();
    
    int size();
}
