package org.memgraphd;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class GraphInvocationHandler implements InvocationHandler {
    private final Graph graph;
  
    public GraphInvocationHandler(Graph graph) {
        this.graph = graph;
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      
        if(method.getDeclaringClass() == GraphSupervisor.class
                || method.getDeclaringClass() == GraphLifecycleListenerManager.class) {
            
            if(method.getName().equals("start")) {
                onStart();
            }
            else if(method.getName().equals("stop")) {
                onStop();
            }
            else if(method.getName().equals("clear")) {
                onClear();
            }
            return invokeGraph(method, args);
        }
        authorize();
        return invokeGraph(method, args);
    }

    private Object invokeGraph(Method method, Object[] args) throws Throwable {
        try {
            return method.invoke(graph, args);
        } catch (Exception e) {
            if(e.getCause() != null) {
                throw e.getCause();
            }
            throw e;
        }
    }
    
    private void authorize() {
        if(!graph.isRunning()) {
            throw new RuntimeException("Request cannot be handled. memgraphd is stopped.");
        }
    }
    
    private void onStart() {
        if(graph.isRunning()) {
            throw new RuntimeException("memgraphd is already running");
        }
    }
    
    private void onStop() {
        if(graph.isStopped()) {
            throw new RuntimeException("memgraphd is already stopped.");
        }
        if(!graph.isRunning()) {
            throw new RuntimeException("memgraphd is not running.");
        }
    }
    
    private void onClear() {
        if(graph.isStopped()) {
            throw new RuntimeException("memgraphd is stopped.");
        }
        if(!graph.isRunning()) {
            throw new RuntimeException("memgraphd is not running");
        }
    }
}
