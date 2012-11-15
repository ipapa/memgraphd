package org.memgraphd;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class GraphInvocationHandler implements InvocationHandler {
    private final Graph graph;
    private final String[] excludeMethods = new String[] { "start", "stop", "register", 
            "unregister", "isRunning", "isStopped", "isInitialized" } ;
    private final Set<String> excludedMethodSet = new HashSet<String>(Arrays.asList(excludeMethods));
    
    public GraphInvocationHandler(Graph graph) {
        this.graph = graph;
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      
        if(!excludedMethodSet.contains(method.getName())) {
            authorize();
        }
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
}
