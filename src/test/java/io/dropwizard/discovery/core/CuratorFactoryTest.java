package io.dropwizard.discovery.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CompressionProvider;
import org.apache.curator.test.TestingServer;
import org.junit.Before;
import org.junit.Test;

import com.codahale.metrics.health.HealthCheckRegistry;

import io.dropwizard.discovery.DiscoveryFactory;
import io.dropwizard.discovery.health.CuratorHealthCheck;
import io.dropwizard.discovery.manage.CuratorManager;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.setup.Environment;
import io.dropwizard.util.Duration;

public class CuratorFactoryTest {

    private final Environment environment = mock(Environment.class);
    private final LifecycleEnvironment lifecycle = mock(LifecycleEnvironment.class);
    private final HealthCheckRegistry healthchecks = mock(HealthCheckRegistry.class);
    private final DiscoveryFactory discoveryFactory = mock(DiscoveryFactory.class);
    private final RetryPolicy retryPolicy = mock(RetryPolicy.class);
    private final CompressionProvider compressionProvider = mock(CompressionProvider.class);

    private final Duration connectionTimeout = Duration.milliseconds(12);
    private final Duration sessionTimeout = Duration.milliseconds(34);
    private final boolean isReadOnly = false;
    private final String namespace = "namespace";

    TestingServer testingServer;

    private CuratorFactory factory;

    @Before
    public void setup() throws Exception {
        testingServer = new TestingServer();

        reset(environment);
        when(environment.lifecycle()).thenReturn(lifecycle);
        when(environment.healthChecks()).thenReturn(healthchecks);

        reset(discoveryFactory);
        when(discoveryFactory.getConnectionTimeout()).thenReturn(connectionTimeout);
        when(discoveryFactory.getSessionTimeout()).thenReturn(sessionTimeout);
        when(discoveryFactory.getRetryPolicy()).thenReturn(retryPolicy);
        when(discoveryFactory.getCompressionProvider()).thenReturn(compressionProvider);
        when(discoveryFactory.getQuorumSpec()).thenReturn(testingServer.getConnectString());
        when(discoveryFactory.isReadOnly()).thenReturn(isReadOnly);
        when(discoveryFactory.getNamespace()).thenReturn(namespace);

        factory = new CuratorFactory(environment);
    }

    @Test
    public void testConstructorInvalidEnvironment() {
        try {
            new CuratorFactory(null);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException npe) {
        }
    }

    @Test
    public void testBuild() {
        factory.build(discoveryFactory);
        verify(lifecycle).manage(any(CuratorManager.class));
        verify(healthchecks).register(eq("curator"), any(CuratorHealthCheck.class));

        // The hope is that calling these methods means that they are being used
        // We aren't guaranteeing that they are being used properly.
        verify(discoveryFactory).getConnectionTimeout();
        verify(discoveryFactory).getSessionTimeout();
        verify(discoveryFactory).getRetryPolicy();
        verify(discoveryFactory).getCompressionProvider();
        verify(discoveryFactory).getQuorumSpec();
        verify(discoveryFactory).isReadOnly();
        verify(discoveryFactory).getNamespace();
    }

    @Test
    public void testConnectionTimeout() {
        CuratorFramework curatorFramework = factory.build(discoveryFactory);
        assertThat(curatorFramework.getZookeeperClient().getConnectionTimeoutMs())
                .isEqualTo(connectionTimeout.toMilliseconds());
    }

    // TODO: session timeout

    @Test
    public void testRetryPolicy() {
        CuratorFramework curatorFramework = factory.build(discoveryFactory);
        assertThat(curatorFramework.getZookeeperClient().getRetryPolicy()).isEqualTo(retryPolicy);
    }

    // TODO: compression provider

    @Test
    public void testQuorumSpec() {
        CuratorFramework curatorFramework = factory.build(discoveryFactory);
        assertThat(curatorFramework.getZookeeperClient().getCurrentConnectionString())
                .isEqualTo(testingServer.getConnectString());
    }

    // TODO: read-only

    @Test
    public void testNamespace() {
        CuratorFramework curatorFramework = factory.build(discoveryFactory);
        assertThat(curatorFramework.getNamespace()).isEqualTo(namespace);
    }
}