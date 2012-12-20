package org.memgraphd.data.library;

import java.util.Set;


public interface LibrarySection {
    
    String getName();
    
    int getSize();
    
    Set<Category> getCategories();
    
    FilterableDataCollection getAllData();
    
}
