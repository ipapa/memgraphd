package org.memgraphd;

import java.lang.reflect.Proxy;

import org.memgraphd.data.Data;
import org.memgraphd.data.GraphData;
import org.memgraphd.data.GraphDataSnapshotManager;
import org.memgraphd.data.GraphDataSnapshotManagerImpl;
import org.memgraphd.data.event.GraphDataEventListenerManagerImpl;
import org.memgraphd.data.library.Library;
import org.memgraphd.data.relationship.DataMatchmaker;
import org.memgraphd.data.relationship.DataMatchmakerImpl;
import org.memgraphd.decision.Sequence;
import org.memgraphd.exception.GraphException;
import org.memgraphd.memory.MemoryAccess;
import org.memgraphd.memory.MemoryBlock;
import org.memgraphd.memory.MemoryManager;
import org.memgraphd.memory.MemoryManagerImpl;
import org.memgraphd.memory.MemoryReference;
import org.memgraphd.memory.MemoryStats;
import org.memgraphd.memory.operation.MemoryOperations;
import org.memgraphd.operation.GraphFilter;
import org.memgraphd.operation.GraphFilterImpl;
import org.memgraphd.operation.GraphReader;
import org.memgraphd.operation.GraphReaderImpl;
import org.memgraphd.operation.GraphSeeker;
import org.memgraphd.operation.GraphSeekerImpl;
import org.memgraphd.operation.GraphStateManager;
import org.memgraphd.operation.GraphStateManagerImpl;
import org.memgraphd.operation.GraphWriter;
import org.memgraphd.operation.GraphWriterImpl;
import org.memgraphd.security.GraphAuthority;
import org.memgraphd.security.GraphAuthorityImpl;
import org.memgraphd.security.GraphRequestResolver;
import org.memgraphd.security.GraphRequestResolverImpl;
import org.memgraphd.security.GraphValidator;
import org.memgraphd.security.GraphValidatorImpl;

/**
 * This is the default implementation of {@link Graph} that brings all
 * functionality together. You need to instantiate and than
 * {@link GraphImpl#run()} the {@link Graph} before you can start using it to
 * store and retrieve data.
 * 
 * @author Ilirjan Papa
 * @since August 17, 2012
 * 
 */
public final class GraphImpl implements Graph {
    
    private final String name;
    private final GraphMappings mappings;
    private final GraphFilter filter;
    private final GraphReader reader;
    private final GraphWriter writer;
    private final GraphSeeker seeker;
    private final GraphStateManager stateManager;
    private final GraphAuthority authority;
    private final GraphValidator validator;
    private final GraphRequestResolver resolver;
    private final MemoryOperations memoryAccess;
    private final DataMatchmaker dataMatchmaker;
    private final GraphSupervisor supervisor;
    private final GraphConfig config;
    private final Library library;
    
    private GraphImpl(GraphConfig config) {
        this.config = config;
        this.name = config.getName();
        MemoryManager memoryManager = new MemoryManagerImpl(config.getMemoryBlockResolver());
        this.memoryAccess = new MemoryAccess(memoryManager);
        this.mappings = new GraphMappingsImpl();
        
        this.seeker = new GraphSeekerImpl(memoryAccess, mappings);
        this.reader = new GraphReaderImpl(memoryAccess, seeker);
        this.dataMatchmaker = new DataMatchmakerImpl(memoryAccess, seeker);
        this.stateManager = new GraphStateManagerImpl(memoryAccess, mappings, config.getLibrarian(), 
                                dataMatchmaker, new GraphDataEventListenerManagerImpl());
        this.authority = new GraphAuthorityImpl();
        this.validator = new GraphValidatorImpl(config.getDecisionMaker());
        this.resolver = new GraphRequestResolverImpl(reader);
        this.writer = new GraphWriterImpl(memoryAccess, authority, validator,
                resolver, config.getDecisionMaker(), stateManager);
        this.filter = new GraphFilterImpl(memoryAccess, reader);
        
        GraphDataSnapshotManager snapshotManager = new GraphDataSnapshotManagerImpl(reader, writer, mappings, config.getDecisionMaker(), stateManager);
        this.supervisor = new GraphSupervisorImpl(snapshotManager, (MemoryStats) memoryManager);
        this.library = (Library) config.getLibrarian();
    }

    private static final Graph createProxy(Graph liveGraph) {
        return (Graph) Proxy.newProxyInstance(GraphImpl.class.getClassLoader(),
                new Class[] { Graph.class }, new GraphInvocationHandler(liveGraph));
    }
    
    /**
     * Constructs an immutable instance of {@link Graph}.
     * @param config {@link GraphConfig}
     * @return {@link Graph}
     * @throws GraphException
     */
    public static final Graph build(GraphConfig config) throws GraphException {
        Graph graph = createProxy(new GraphImpl(config));
        graph.initialize();
        return graph;
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
    public GraphData readId(String id) {
        return reader.readId(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraphData readSequence(Sequence seq) {
        return reader.readSequence(seq);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraphData readReference(MemoryReference ref) {
        return reader.readReference(ref);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GraphData readGraph(String id) {
        return reader.readGraph(id);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GraphData readGraph(Sequence seq) {
        return reader.readGraph(seq);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public GraphData readGraph(MemoryReference ref) {
        return reader.readGraph(ref);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MemoryReference write(Data data) throws GraphException {
        return writer.write(data);
    }

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public void delete(String id) throws GraphException {
        writer.delete(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraphData[] filterBy(MemoryBlock block) {
        return filter.filterBy(block);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraphData[] filterByRange(MemoryReference startRef, MemoryReference endRef) {
        return filter.filterByRange(startRef, endRef);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraphData[] filterByRange(Sequence startSeq, Sequence endSeq) {
        return filter.filterByRange(startSeq, endSeq);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final Library getLibrary() {
        return library;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void run() throws GraphException {
        if(!config.getBookKeeper().isBookOpen()) {
            config.getBookKeeper().openBook();
        }
        supervisor.run();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void shutdown() throws GraphException {
        if(config.getBookKeeper().isBookOpen()) {
            config.getBookKeeper().closeBook();
        }
        supervisor.shutdown();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInitialized() {
        return supervisor.isInitialized();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRunning() {
        return supervisor.isRunning();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isShutdown() {
        return supervisor.isShutdown();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return supervisor.isEmpty();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void register(GraphLifecycleHandler handler) {
        supervisor.register(handler);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void unregister(GraphLifecycleHandler handler) {
        supervisor.unregister(handler);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int capacity() {
        return supervisor.capacity();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int occupied() {
        return supervisor.occupied();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int available() {
        return supervisor.available();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int recycled() {
        return supervisor.recycled();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize() throws GraphException {
        if(!config.getBookKeeper().isBookOpen()) {
            config.getBookKeeper().openBook();
        }
        supervisor.initialize();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() throws GraphException {
        supervisor.clear();
    }

}
