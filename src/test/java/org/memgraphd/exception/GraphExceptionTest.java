package org.memgraphd.exception;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class GraphExceptionTest {
    
    private GraphException exception;

    @Test
    public void testGraphExceptionString() {
        exception = new GraphException("some message");
        assertNotNull(exception);
        assertEquals("some message", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void testGraphExceptionStringThrowable() {
        Throwable cause = new IllegalArgumentException();
        exception = new GraphException("some message", cause);
        assertNotNull(exception);
        assertEquals("some message", exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    public void testGraphExceptionThrowable() {
        Throwable cause = new IllegalArgumentException("some message");
        exception = new GraphException(cause);
        assertNotNull(exception.getMessage());
        assertSame(cause, exception.getCause());
    }

}
