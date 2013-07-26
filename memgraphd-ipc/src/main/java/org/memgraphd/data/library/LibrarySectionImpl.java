package org.memgraphd.data.library;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * A simple implementation of {@link LibrarySection} interface.
 * 
 * @author Ilirjan Papa
 * @since November 20, 2012
 *
 */
public class LibrarySectionImpl implements LibrarySection {
    private final String name;
    private Set<Category> categories;
    
    /**
     * Constructs an instance of {@link LibrarySectionImpl}.
     * @param name section name as {@link String}
     * @param categories list of {@link Category}
     */
    public LibrarySectionImpl(String name, Category[] categories) {
        this.name = name;
        this.categories = categories == null ? new CopyOnWriteArraySet<Category>() 
                : new CopyOnWriteArraySet<Category>(Arrays.asList(categories));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final String getName() {
        return name;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final Set<Category> getCategories() {
        return categories;
    }

}
