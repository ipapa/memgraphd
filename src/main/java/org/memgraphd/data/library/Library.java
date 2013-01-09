package org.memgraphd.data.library;

import org.memgraphd.data.GraphData;
import org.memgraphd.data.library.collection.LibraryDataCollection;

/**
 * A library of {@link GraphData} where data is divided into sections and each section has a 
 * a list of available categories. The {@link Librarian} has exclusive write-access in the {@link Library}.
 * 
 * @author Ilirjan Papa
 * @since November 20, 2012
 *
 */
public interface Library {
    
    /**
     * Returns a list of all available sections that make up this library.
     * @return array of {@link LibrarySection}
     */
    LibrarySection[] getAvailableSections();
    
    /**
     * Lookup a {@link LibraryDataCollection} by {@link LibrarySection}.
     * @param section {@link LibrarySection}
     * @return {@link LibraryDataCollection}
     */
    LibraryDataCollection getSection(LibrarySection section);
    
    /**
     * Returns how many items are stored in this {@link Library}.
     * @return integer
     */
    int size();
    
}
