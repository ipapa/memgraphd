package org.memgraphd.operation;

import org.memgraphd.data.Data;
import org.memgraphd.data.GraphData;
import org.memgraphd.decision.Sequence;
import org.memgraphd.memory.MemoryReference;

import com.sun.corba.se.impl.orbutil.graph.Graph;
/**
 * Responsible for granting read-access-only to {@link GraphData} in our {@link Graph}.
 * 
 * @author Ilirjan Papa
 * @since August 1, 2012
 *
 */
public interface GraphReader {
    
    /**
     * Read {@link GraphData} by {@link Data#getId()}. <b>Note</b>: This method only returns
     * the {@link Data} object <u>without</u> the data relationships in the graph.
     * @param id {@link String}
     * @return {@link GraphData}
     */
    GraphData readId(String id);
    
    /**
     * Read {@link GraphData} by {@link Sequence}. <b>Note</b>: This method only returns
     * the {@link Data} object <u>without</u> the data relationships in the graph.
     * @param seq {@link Sequence}
     * @return {@link GraphData}
     */
    GraphData readSequence(Sequence seq);
    
    /**
     * Read {@link GraphData} by memory reference. <b>Note</b>: This method only returns
     * the {@link Data} object <u>without</u> the data relationships in the graph.
     * @param ref {@link MemoryReference}
     * @return {@link GraphData}
     */
    GraphData readReference(MemoryReference ref);
    
    /**
     * Read {@link GraphData} by {@link Data#getId()}. <b>Note:<b> It will return
     * the complete graph: {@link Data} object plus <u>all</u> its relationships.
     * @param id {@link String}
     * @return {@link GraphData}
     */
    GraphData readGraph(String id);
    
    /**
     * Read {@link GraphData} by {@link Sequence}. <b>Note:<b> It will return
     * the complete graph: {@link Data} object plus <u>all</u> its relationships.
     * @param seq {@link Sequence}
     * @return {@link GraphData}
     */
    GraphData readGraph(Sequence seq);
    
    /**
     * Read {@link GraphData} by memory reference. <b>Note:<b> It will return
     * the complete graph: {@link Data} object plus <u>all</u> its relationships.
     * @param ref {@link MemoryReference}
     * @return {@link GraphData}
     */
    GraphData readGraph(MemoryReference ref);
    
}
