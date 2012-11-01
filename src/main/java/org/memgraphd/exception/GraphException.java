package org.memgraphd.exception;

/**
 * Generic Graph {@link Exception}. All other types exceptions should extends this one.
 * 
 * @author Ilirjan Papa
 * @since July 3, 2012
 *
 */
public class GraphException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public GraphException(String message) {
        super(message);
    }
    
    public GraphException(String message, Throwable throwable) {
        super(message, throwable);
    }
    
    public GraphException(Throwable throwable) {
        super(throwable);
    }
}
