package org.memgraphd.data.library;

import java.util.Set;

import org.memgraphd.data.GraphData;

/**
 * A {@link LibrarySection} is made up of related data that have something in common.
 * Another practical definition of a {@link LibrarySection} is a set of {@link GraphData} {@link Category}(s).
 * 
 * @author Ilirjan Papa
 * @since November 20, 2012
 *
 */
public interface LibrarySection {
    
    /**
     * Returns section's name which has to be unique
     * @return {@link String}
     */
    String getName();
    
    /**
     * Returns a set of data categories.
     * @return {@link Set} of {@link Category}.
     */
    Set<Category> getCategories();
    
}
