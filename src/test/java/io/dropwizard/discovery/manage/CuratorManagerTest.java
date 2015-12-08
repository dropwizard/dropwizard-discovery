package io.dropwizard.discovery.manage;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.apache.curator.CuratorZookeeperClient;
import org.apache.curator.RetryLoop;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CreateBuilder;
import org.apache.curator.framework.api.ProtectACLCreateModePathAndBytesable;
import org.junit.Before;
import org.junit.Test;

public class CuratorManagerTest {

    private final CuratorFramework framework = mock(CuratorFramework.class);
    private final CuratorZookeeperClient client = mock(CuratorZookeeperClient.class);
    private final CreateBuilder create = mock(CreateBuilder.class);
    @SuppressWarnings("unchecked")
    private final ProtectACLCreateModePathAndBytesable<String> acl = mock(ProtectACLCreateModePathAndBytesable.class);
    private final RetryLoop loop = mock(RetryLoop.class);
    private final CuratorManager manager = new CuratorManager(framework);

    @Before
    public void setUp() {
        when(framework.getZookeeperClient()).thenReturn(client);
        when(client.newRetryLoop()).thenReturn(loop);
        when(framework.create()).thenReturn(create);
        when(create.creatingParentsIfNeeded()).thenReturn(acl);
        when(loop.shouldContinue()).thenReturn(true).thenReturn(false);
    }

    @Test
    public void testStart() throws Exception {
        manager.start();
        verify(framework).start();
    }

    @Test
    public void testStop() throws Exception {
        manager.stop();
        verify(framework).close();
    }
}
