package org.memgraphd.data.library;


public interface Library {
    
    LibrarySection[] getAvailableSections();
    
    Librarian getLibrarian();
    
    int size();
    
}
