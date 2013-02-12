package org.memgraphd.security;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.memgraphd.GraphRequestType;
import org.memgraphd.data.Data;
import org.memgraphd.data.GraphData;
import org.memgraphd.data.ReadOnlyData;
import org.memgraphd.data.ReadOnlyExpiringData;
import org.memgraphd.data.ReadOnlyProtectedData;
import org.memgraphd.data.ReadWriteData;
import org.memgraphd.data.ReadWriteExpiringData;
import org.memgraphd.data.ReadWriteProtectedData;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GraphAuthorityImplTest {
    private GraphAuthority authority;
    
    private Data readOnly, readOnlyProtected, readWrite, readWriteProtected, readOnlyExpiring, readWriteExpiring;
    
    @Mock
    private Data nonWritableData;
    
    @Mock
    private GraphData graphData, graphData2;
    
    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    @Before
    public void setUp() throws Exception {
        authority = new GraphAuthorityImpl();
       
        readOnly = new ReadOnlyData("id", DateTime.now());
        readOnlyProtected = new ReadOnlyProtectedData("id", DateTime.now());
        readOnlyExpiring = new ReadOnlyExpiringData("id", DateTime.now(), DateTime.now());
        readWrite = new ReadWriteData("id", DateTime.now(), DateTime.now());
        readWriteProtected = new ReadWriteProtectedData("id", DateTime.now(), DateTime.now());
        readWriteExpiring = new ReadWriteExpiringData("id", DateTime.now(), DateTime.now());
    }

    @Test
    public void testGrantDelete_readOnly() {
        when(graphData.getData()).thenReturn(readOnly);
        
        authority.authorize(new GraphRequestContext(GraphRequestType.DELETE, readOnly, graphData));
        
        verify(graphData).getData();
    }
    
    @Test
    public void testGrantDelete_readOnlyExpiring() {
        when(graphData.getData()).thenReturn(readOnlyExpiring);
        
        authority.authorize(new GraphRequestContext(GraphRequestType.DELETE, readOnlyExpiring, graphData));
        
        verify(graphData).getData();
    }
    
    @Test
    public void testGrantDelete_readWrite() {
        when(graphData.getData()).thenReturn(readWrite);
        
        authority.authorize(new GraphRequestContext(GraphRequestType.DELETE, readWrite, graphData));
        
        verify(graphData).getData();
    }
    
    @Test
    public void testGrantDelete_readWriteExpiring() {
        when(graphData.getData()).thenReturn(readWriteExpiring);
        
        authority.authorize(new GraphRequestContext(GraphRequestType.DELETE, readWriteExpiring, graphData));
        
        verify(graphData).getData();
    }
    
    @Test
    public void testGrantDelete_readOnlyProtected() {
        exception.expect(RuntimeException.class);
        exception.expectMessage("Data id=id is protected and cannot be deleted.");
        
        when(graphData.getData()).thenReturn(readOnlyProtected);
        
        authority.authorize(new GraphRequestContext(GraphRequestType.DELETE, readOnlyProtected, graphData));
        
        verify(graphData).getData();
    }
    
    @Test
    public void testGrantDelete_readWriteProtected() {
        exception.expect(RuntimeException.class);
        exception.expectMessage("Data id=id is protected and cannot be deleted.");
        
        when(graphData.getData()).thenReturn(readWriteProtected);
        
        authority.authorize(new GraphRequestContext(GraphRequestType.DELETE, readWriteProtected, graphData));
        
        verify(graphData).getData();
    }

    @Test
    public void testGrantCreate() {
        authority.authorize(new GraphRequestContext(GraphRequestType.CREATE, readOnly, null));
        authority.authorize(new GraphRequestContext(GraphRequestType.CREATE, readOnlyProtected, null));
        authority.authorize(new GraphRequestContext(GraphRequestType.CREATE, readOnlyExpiring, null));
        authority.authorize(new GraphRequestContext(GraphRequestType.CREATE, readWrite, null));
        authority.authorize(new GraphRequestContext(GraphRequestType.CREATE, readWriteProtected, null));
        authority.authorize(new GraphRequestContext(GraphRequestType.CREATE, readWriteExpiring, null));
    }
    
    @Test
    public void testGrantCreate_nonWritable() {
        when(nonWritableData.getId()).thenReturn("id");
        when(nonWritableData.canWrite()).thenReturn(false);
        exception.expect(RuntimeException.class);
        exception.expectMessage("Data id=id permissions do not allow it to be stored.");
        
        authority.authorize(new GraphRequestContext(GraphRequestType.CREATE, nonWritableData, null));
    }

    @Test
    public void testGrantUpdate() {
        when(graphData.getData()).thenReturn(readWrite);
        when(graphData2.getData()).thenReturn(readWriteExpiring);
        authority.authorize(new GraphRequestContext(GraphRequestType.UPDATE, readWrite, graphData));
        authority.authorize(new GraphRequestContext(GraphRequestType.UPDATE, readWriteExpiring, graphData2));
    }
    
    @Test
    public void testGrantUpdate_readOnly() {
        when(graphData.getData()).thenReturn(readOnly);
        exception.expect(RuntimeException.class);
        exception.expectMessage("Existing data id=id permissions do not allow an update.");
        
        authority.authorize(new GraphRequestContext(GraphRequestType.UPDATE, readOnly, graphData));
    }
    
    @Test
    public void testGrantUpdate_readOnlyProtected() {
        when(graphData.getData()).thenReturn(readOnlyProtected);
        
        exception.expect(RuntimeException.class);
        exception.expectMessage("Existing data id=id permissions do not allow an update.");
        
        authority.authorize(new GraphRequestContext(GraphRequestType.UPDATE, readOnlyProtected, graphData));
    }
    
    @Test
    public void testGrantUpdate_readOnlyExpiring() {
        when(graphData.getData()).thenReturn(readOnlyExpiring);
        
        exception.expect(RuntimeException.class);
        exception.expectMessage("Existing data id=id permissions do not allow an update.");
        
        authority.authorize(new GraphRequestContext(GraphRequestType.UPDATE, readOnlyExpiring, graphData));
    }
    
    @Test
    public void testGrantUpdate_readWriteProtected() {
        when(graphData.getData()).thenReturn(readWriteProtected);
        exception.expect(RuntimeException.class);
        exception.expectMessage("Data id=id is protected and cannot be deleted.");
        
        authority.authorize(new GraphRequestContext(GraphRequestType.UPDATE, readWriteProtected, graphData));
    }
}
