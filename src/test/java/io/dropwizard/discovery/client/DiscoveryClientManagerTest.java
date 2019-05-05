package io.dropwizard.discovery.client;

import org.junit.Test;

import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DiscoveryClientManagerTest {

    @SuppressWarnings("unchecked")
    DiscoveryClient<String> client = mock(DiscoveryClient.class);

    DiscoveryClientManager<String> manager = new DiscoveryClientManager<String>(client);

    @Test
    public void testConstructorInvalidClient() {
        try {
            new DiscoveryClientManager<String>(null);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException npe) {
        }
    }

    @Test
    public void testStart() throws Exception {
        manager.start();
        verify(client).start();
    }

    @Test
    public void testStop() throws Exception {
        manager.stop();
        verify(client).close();
    }
}
