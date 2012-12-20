package org.memgraphd.data.library;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class LibrarySectionImpl implements LibrarySection {
    private final String name;
    private Set<Category> categories;
    
    public LibrarySectionImpl(String name, Category[] categories) {
        this.name = name;
        this.categories = categories == null ? new CopyOnWriteArraySet<Category>() 
                : new CopyOnWriteArraySet<Category>(Arrays.asList(categories));
    }
    
    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final int getSize() {
        int size = 0;
        for(Category c : categories) {
            size += c.size();
        }
        return size;
    }

    @Override
    public final Set<Category> getCategories() {
        return categories;
    }

    @Override
    public FilterableDataCollection getAllData() {
        // TODO Auto-generated method stub
        return null;
    }

}
