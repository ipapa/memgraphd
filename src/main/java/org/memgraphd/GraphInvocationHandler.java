package org.memgraphd;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.memgraphd.data.GraphDataSnapshotManager;

public class GraphInvocationHandler implements InvocationHandler {
    private final Graph graph;
  
    public GraphInvocationHandler(Graph graph) {
        this.graph = graph;
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      
        if(method.getDeclaringClass() == GraphSupervisor.class
                || method.getDeclaringClass() == GraphLifecycleListenerManager.class
                || method.getDeclaringClass() == GraphDataSnapshotManager.class) {
            
            if(method.getName().equals("initialize")) {
                onInitialize();
            }
            else if(method.getName().equals("run")) {
                onRun();
            }
            else if(method.getName().equals("shutdown")) {
                onShutdown();
            }
            else if(method.getName().equals("clear")) {
                onClear();
            }
            return invokeGraph(method, args);
        }
        if(!graph.isRunning()) {
            throw new RuntimeException("Request cannot be handled. memgraphd is stopped.");
        }
        return invokeGraph(method, args);
    }

    protected Object invokeGraph(Method method, Object[] args) throws Throwable {
        try {
            return method.invoke(graph, args);
        } catch (Exception e) {
            if(e.getCause() != null) {
                throw e.getCause();
            }
            throw e;
        }
    }
    
    private void onInitialize() {
        if(graph.isRunning()) {
            throw new RuntimeException("memgraphd is already running");
        }
        if(graph.isShutdown()) {
            throw new RuntimeException("memgraphd is shut down");
        }
    }
    
    private void onRun() {
        if(graph.isRunning()) {
            throw new RuntimeException("memgraphd is already running");
        }
    }
    
    private void onShutdown() {
        if(graph.isShutdown()) {
            throw new RuntimeException("memgraphd is already stopped.");
        }
        if(!graph.isRunning()) {
            throw new RuntimeException("memgraphd is not running.");
        }
    }
    
    private void onClear() {
        if(graph.isShutdown()) {
            throw new RuntimeException("memgraphd is shut down.");
        }
        if(!graph.isRunning()) {
            throw new RuntimeException("memgraphd is not running");
        }
    }
}
