package io.dropwizard.discovery;

import io.dropwizard.util.Duration;
import org.apache.curator.RetryPolicy;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

public class DiscoveryFactoryTest {
    private final DiscoveryFactory factory = new DiscoveryFactory();

    @Test
    public void testGetHosts() {
        assertThat(factory.getHosts()).containsOnly("localhost");
    }

    @Test
    public void testGetPort() {
        assertThat(factory.getPort()).isEqualTo(2181);
    }

    @Test
    public void testGetQuorumSpec() {
        assertThat(factory.getQuorumSpec()).isEqualTo("localhost:2181");
    }

    @Test
    public void testServiceNameAccessors() {
        assertThat(factory.getServiceName()).isEqualTo("");
        factory.setServiceName("test-service-name");
        try {
            factory.setServiceName(null);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (final NullPointerException ignore) {
        }
        assertThat(factory.getServiceName()).isEqualTo("test-service-name");
    }

    @Test
    public void testNamespaceAccessors() {
        assertThat(factory.getNamespace()).isEqualTo("dropwizard");
        factory.setNamespace("test-namespace");
        try {
            factory.setNamespace(null);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (final NullPointerException ignore) {
        }
        assertThat(factory.getNamespace()).isEqualTo("test-namespace");
    }

    @Test
    public void testBasePathAccessors() {
        assertThat(factory.getBasePath()).isEqualTo("service");
        factory.setBasePath("test-base-path");
        try {
            factory.setBasePath(null);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (final NullPointerException ignore) {
        }
        assertThat(factory.getBasePath()).isEqualTo("test-base-path");
    }

    @Test
    public void testListenAddressAccessors() {
        assertThat(factory.getListenAddress()).isEqualTo("");
        factory.setListenAddress("0.0.0.0");
        try {
            factory.setListenAddress(null);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (final NullPointerException ignore) {
        }
        assertThat(factory.getListenAddress()).isEqualTo("0.0.0.0");
    }

    @Test
    public void testConnectionTimeoutAccessors() {
        assertThat(factory.getConnectionTimeout()).isEqualTo(Duration.seconds(6));
        factory.setConnectionTimeout(Duration.minutes(30));
        try {
            factory.setConnectionTimeout(null);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (final NullPointerException ignore) {
        }
        assertThat(factory.getConnectionTimeout()).isEqualTo(Duration.minutes(30));
    }

    @Test
    public void testSessionTimeoutAccessors() {
        assertThat(factory.getSessionTimeout()).isEqualTo(Duration.seconds(6));
        factory.setSessionTimeout(Duration.minutes(30));
        try {
            factory.setSessionTimeout(null);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (final NullPointerException ignore) {
        }
        assertThat(factory.getSessionTimeout()).isEqualTo(Duration.minutes(30));
    }

    @Test
    public void testIsDisabledAccessors() {
        assertThat(factory.isDisabled()).isFalse();
        factory.setIsDisabled(true);
        assertThat(factory.isDisabled()).isTrue();
    }

    @Test
    public void testIsReadOnlyAccessors() {
        assertThat(factory.isReadOnly()).isFalse();
        factory.setIsReadOnly(true);
        assertThat(factory.isReadOnly()).isTrue();
    }

    @Test
    public void testMaxRetriesAccessors() {
        assertThat(factory.getMaxRetries()).isEqualTo(5);
        factory.setMaxRetries(20);
        assertThat(factory.getMaxRetries()).isEqualTo(20);
    }

    @Test
    public void testBaseSleepTimeAccessors() {
        assertThat(factory.getBaseSleepTime()).isEqualTo(Duration.seconds(1));
        factory.setBaseSleepTime(Duration.minutes(30));
        try {
            factory.setBaseSleepTime(null);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (final NullPointerException ignore) {
        }
        assertThat(factory.getBaseSleepTime()).isEqualTo(Duration.minutes(30));
    }

    @Test
    public void testGetRetryPolicy() {
        DiscoveryFactory factory = new DiscoveryFactory();
        RetryPolicy retryPolicy = factory.getRetryPolicy();
        assertThat(retryPolicy).isInstanceOf(ExponentialBackoffRetry.class);
        ExponentialBackoffRetry backoffPolicy = (ExponentialBackoffRetry) retryPolicy;
        assertThat(backoffPolicy.getBaseSleepTimeMs()).isEqualTo(factory.getBaseSleepTime().toMilliseconds());
        assertThat(backoffPolicy.getN()).isEqualTo(factory.getMaxRetries());
    }

    @Test
    public void testGetCompressionProvider() {
        assertThat(factory.getCompressionProvider()).isEqualTo(DiscoveryFactory.CompressionCodec.GZIP.getProvider());
    }
}
