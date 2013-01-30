package org.memgraphd.operation;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.memgraphd.data.Data;
import org.memgraphd.data.ReadOnlyData;
import org.memgraphd.data.ReadOnlyExpiringData;
import org.memgraphd.data.ReadOnlyProtectedData;
import org.memgraphd.data.ReadWriteData;
import org.memgraphd.data.ReadWriteExpiringData;
import org.memgraphd.data.ReadWriteProtectedData;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GraphAuthorityImplTest {
    private GraphAuthority authority;
    
    private Data readOnly, readOnlyProtected, readWrite, readWriteProtected, readOnlyExpiring, readWriteExpiring;
    
    @Mock
    private Data nonWritableData;
    
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
    public void testGrantDelete() {
        authority.grantDelete(readOnly);
        authority.grantDelete(readOnlyExpiring);
        authority.grantDelete(readWrite);
        authority.grantDelete(readWriteExpiring);
    }
    
    @Test
    public void testGrantDelete_readOnlyProtected() {
        exception.expect(RuntimeException.class);
        exception.expectMessage("Data id=id is protected and cannot be deleted.");
        
        authority.grantDelete(readOnlyProtected);
    }
    
    @Test
    public void testGrantDelete_readWriteProtected() {
        exception.expect(RuntimeException.class);
        exception.expectMessage("Data id=id is protected and cannot be deleted.");
        
        authority.grantDelete(readWriteProtected);
    }

    @Test
    public void testGrantInsert() {
        authority.grantInsert(readOnly);
        authority.grantInsert(readOnlyProtected);
        authority.grantInsert(readOnlyExpiring);
        authority.grantInsert(readWrite);
        authority.grantInsert(readWriteProtected);
        authority.grantInsert(readWriteExpiring);
    }
    
    @Test
    public void testGrantInsert_nonWritable() {
        when(nonWritableData.getId()).thenReturn("id");
        when(nonWritableData.canWrite()).thenReturn(false);
        exception.expect(RuntimeException.class);
        exception.expectMessage("Data id=id permissions do not allow it to be stored.");
        
        authority.grantInsert(nonWritableData);
    }

    @Test
    public void testGrantUpdate() {
        authority.grantUpdate(readWrite, readWrite);
        authority.grantUpdate(readWriteExpiring, readWriteExpiring);
    }
    
    @Test
    public void testGrantUpdate_readOnly() {
        exception.expect(RuntimeException.class);
        exception.expectMessage("Existing data id=id permissions do not allow an update.");
        
        authority.grantUpdate(readOnly, readOnly);
    }
    
    @Test
    public void testGrantUpdate_readOnlyProtected() {
        exception.expect(RuntimeException.class);
        exception.expectMessage("Existing data id=id permissions do not allow an update.");
        
        authority.grantUpdate(readOnlyProtected, readOnlyProtected);
    }
    
    @Test
    public void testGrantUpdate_readOnlyExpiring() {
        exception.expect(RuntimeException.class);
        exception.expectMessage("Existing data id=id permissions do not allow an update.");
        
        authority.grantUpdate(readOnlyExpiring, readOnlyExpiring);
    }
    
    @Test
    public void testGrantUpdate_readWriteProtected() {
        exception.expect(RuntimeException.class);
        exception.expectMessage("Data id=id is protected and cannot be deleted.");
        
        authority.grantUpdate(readWriteProtected, readWriteProtected);
    }
}
