package io.dropwizard.discovery.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import io.dropwizard.discovery.DiscoveryFactory;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;
import org.junit.Before;
import org.junit.Test;
import com.google.common.base.Optional;

public class CuratorAdvertiserTest {

    @SuppressWarnings("unchecked")
    private final ServiceDiscovery<InstanceMetadata> discovery = mock(ServiceDiscovery.class);
    private final DiscoveryFactory factory = new DiscoveryFactory();
    private final CuratorAdvertiser<InstanceMetadata> advertiser = new CuratorAdvertiser<InstanceMetadata>(
            factory, discovery, new DefaultServiceInstanceFactory());

    @Before
    public void setUp() {
        factory.setServiceName("test-service");
    }

    @Test
    public void testInitListenInfo() throws Exception {
        factory.setListenAddress("127.0.0.1");
        advertiser.initListenInfo(8080, 8180);
        assertThat(advertiser.getListenPort()).isEqualTo(8080);
        assertThat(advertiser.getAdminPort()).isEqualTo(Optional.of(8180));
        assertThat(advertiser.getListenAddress()).isEqualTo("127.0.0.1");
    }

    @Test
    public void testInitListenInfoNoAdmin() throws Exception {
        factory.setListenAddress("127.0.0.1");
        advertiser.initListenInfo(8080, null);
        assertThat(advertiser.getListenPort()).isEqualTo(8080);
        assertThat(advertiser.getAdminPort()).isEqualTo(
                Optional.<Integer> absent());
        assertThat(advertiser.getListenAddress()).isEqualTo("127.0.0.1");
    }

    @Test
    public void testCheckInitialized() throws Exception {
        try {
            advertiser.checkInitialized();
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (final IllegalStateException ignore) {
        }

        advertiser.initListenInfo(8080, null);
        advertiser.checkInitialized();
    }

    @Test
    public void testRegisterAvailability() throws Exception {
        try {
            advertiser.registerAvailability();
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (final IllegalStateException ignore) {
        }

        advertiser.initListenInfo(8080, null);
        final ServiceInstance<InstanceMetadata> instance = advertiser
                .getInstance();
        advertiser.registerAvailability(instance);
        verify(discovery).registerService(instance);
    }

    @Test
    public void testUnregisterAvailability() throws Exception {
        try {
            advertiser.unregisterAvailability();
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (final IllegalStateException ignore) {
        }

        advertiser.initListenInfo(8080, null);
        final ServiceInstance<InstanceMetadata> instance = advertiser
                .getInstance();
        advertiser.unregisterAvailability(instance);
        verify(discovery).unregisterService(instance);
    }
}
