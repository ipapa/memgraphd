package org.memgraphd.data.library;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.memgraphd.data.GraphData;
import org.memgraphd.data.library.collection.DefaultLibraryDataCollection;
import org.memgraphd.data.library.collection.LibraryDataCollection;
/**
 * A default implementation of a {@link Library}.
 * 
 * @author Ilirjan Papa
 * @since December 26, 2012
 *
 */
public class DefaultLibrary implements Librarian, Library {
    
    private final static Logger LOGGER = Logger.getLogger(DefaultLibrary.class);
    
    private final LibrarySection[] librarySections;
    
    private final Map<LibrarySection, Map<Category, Set<GraphData>>> map;
    
    private final AtomicInteger count;
    
    /**
     * Constructs an instance of {@link Library}.
     * @param sections List of {@link LibrarySection}(s)
     */
    public DefaultLibrary(LibrarySection[] sections) {
        this.librarySections = sections;
        this.map = new ConcurrentHashMap<LibrarySection, Map<Category, Set<GraphData>>>();
        this.count = new AtomicInteger();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final LibrarySection[] getAvailableSections() {
        return librarySections;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final int size() {
        return count.get();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void archive(GraphData gData) {
        boolean archived = false;
        for(LibrarySection section : librarySections) {
            Map<Category, Set<GraphData>> mapping = map.get(section);
            if(mapping == null) {
                mapping = new ConcurrentHashMap<Category, Set<GraphData>>();
                map.put(section, mapping);
            }
            for(Category cat : section.getCategories()) {
                Set<GraphData> dataSet = mapping.get(cat);
                if(dataSet == null) {
                    dataSet = Collections.synchronizedSet(new HashSet<GraphData>());
                    mapping.put(cat, dataSet);
                }
                if(cat.getPredicate().apply(gData)) {
                    LOGGER.info(String.format("Archiving graph data id=%s in section=%s and category=%s", 
                            gData.getData().getId(), section.getName(), cat.getName() ));
                    dataSet.add(gData);
                    archived = true;
                }
            }
        }
        if(archived) {
            count.incrementAndGet();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void unarchive(GraphData gData) {
        boolean unarchived = false;
        for(LibrarySection section : map.keySet()) {
            for(Map.Entry<Category, Set<GraphData>> entry : map.get(section).entrySet()) {
                Category cat = entry.getKey();
                Set<GraphData> catValue = entry.getValue();
                if(catValue.contains(gData)) {
                    LOGGER.info(String.format("Unarchiving graph data id=%s in section=%s and category=%s", 
                            gData.getData().getId(), section.getName(), cat.getName() ));
                    catValue.remove(gData);
                    unarchived = true;
                }
            }              
        }
        if(unarchived) {
            count.decrementAndGet();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public LibraryDataCollection getSection(LibrarySection section) {
        if(section == null) {
            return DefaultLibraryDataCollection.EMPTY;
        }
       
        Map<Category, Set<GraphData>> dataSet = map.get(section);
        
        if(dataSet == null) {
            return DefaultLibraryDataCollection.EMPTY;
        }
        
        Set<GraphData> graphSet = new HashSet<GraphData>();  
        for(Set<GraphData> set : dataSet.values()) {
            graphSet.addAll(set);
        }
        
        return new DefaultLibraryDataCollection(new ArrayList<GraphData>(graphSet));
    }

}
