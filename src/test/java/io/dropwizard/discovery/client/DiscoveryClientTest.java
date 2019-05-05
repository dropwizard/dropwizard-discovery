package io.dropwizard.discovery.client;

import org.apache.curator.x.discovery.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.*;

public class DiscoveryClientTest {

    DiscoveryClient<String> client;

    final String SERVICE_NAME = "service-name";

    @SuppressWarnings("unchecked")
    ServiceInstance<String> instanceOne = mock(ServiceInstance.class);
    @SuppressWarnings("unchecked")
    ServiceInstance<String> instanceTwo = mock(ServiceInstance.class);
    @SuppressWarnings("unchecked")
    ServiceInstance<String> instanceThree = mock(ServiceInstance.class);

    @SuppressWarnings("unchecked")
    ServiceDiscovery<String> discovery = mock(ServiceDiscovery.class);
    DownInstancePolicy downInstancePolicy = mock(DownInstancePolicy.class);
    @SuppressWarnings("unchecked")
    ProviderStrategy<String> providerStrategy = mock(ProviderStrategy.class);
    @SuppressWarnings("unchecked")
    ServiceProvider<String> serviceProvider = mock(ServiceProvider.class);
    @SuppressWarnings("unchecked")
    ServiceCache<String> serviceCache = mock(ServiceCache.class);

    @Before
    public void setup() throws Exception {
        @SuppressWarnings("unchecked")
        ServiceProviderBuilder<String> serviceProviderBuilder = mock(ServiceProviderBuilder.class);
        when(discovery.serviceProviderBuilder()).thenReturn(serviceProviderBuilder);
        when(serviceProviderBuilder.serviceName(anyString())).thenReturn(serviceProviderBuilder);
        when(serviceProviderBuilder.downInstancePolicy(any())).thenReturn(serviceProviderBuilder);
        when(serviceProviderBuilder.providerStrategy(any())).thenReturn(serviceProviderBuilder);
        when(serviceProviderBuilder.build()).thenReturn(serviceProvider);

        @SuppressWarnings("unchecked")
        ServiceCacheBuilder<String> serviceCacheBuilder = mock(ServiceCacheBuilder.class);
        when(discovery.serviceCacheBuilder()).thenReturn(serviceCacheBuilder);
        when(serviceCacheBuilder.name(anyString())).thenReturn(serviceCacheBuilder);
        when(serviceCacheBuilder.build()).thenReturn(serviceCache);

        when(discovery.queryForNames()).thenReturn(Arrays.asList("service-one", "service-two", "service-three"));
        when(discovery.queryForInstances("service-one"))
                .thenReturn(Arrays.asList(instanceOne, instanceTwo, instanceThree));
        when(serviceCache.getInstances()).thenReturn(Arrays.asList(instanceOne, instanceTwo, instanceThree));
        when(serviceProvider.getInstance()).thenReturn(instanceOne);

        client = new DiscoveryClient<String>(SERVICE_NAME, discovery, downInstancePolicy, providerStrategy);
    }

    @SuppressWarnings("resource")
    @Test
    public void testConstructorInvalidServiceName() {
        try {
            new DiscoveryClient<String>(null, discovery, downInstancePolicy, providerStrategy);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException npe) {
        }

        try {
            new DiscoveryClient<String>("", discovery, downInstancePolicy, providerStrategy);
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
        }
    }

    @SuppressWarnings("resource")
    @Test
    public void testConstructorInvalidProviderStrategy() {
        try {
            new DiscoveryClient<String>(SERVICE_NAME, discovery, downInstancePolicy, null);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException npe) {
        }
    }

    @SuppressWarnings("resource")
    @Test
    public void testConstructorInvalidServiceDiscovery() {
        try {
            new DiscoveryClient<String>(SERVICE_NAME, null, downInstancePolicy, providerStrategy);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException npe) {
        }
    }

    @Test
    public void testGetServices() throws Exception {
        assertThat(client.getServices()).containsOnly("service-one", "service-two", "service-three");
    }

    @Test
    public void testGetInstancesByName() throws Exception {
        assertThat(client.getInstances("service-one")).containsOnly(instanceOne, instanceTwo, instanceThree);
    }

    @Test
    public void testGetInstances() {
        assertThat(client.getInstances()).containsOnly(instanceOne, instanceTwo, instanceThree);
    }

    @Test
    public void testGetInstance() throws Exception {
        assertThat(client.getInstance()).isEqualTo(instanceOne);
    }

    @Test
    public void testNoteError() {
        client.noteError(instanceOne);
        verify(serviceProvider).noteError(instanceOne);
    }

    @Test
    public void testStart() throws Exception {
        client.start();
        verify(serviceProvider).start();
        verify(serviceCache).start();
    }

    @Test
    public void testClose() throws Exception {
        client.close();
        verify(serviceCache).close();
        verify(serviceProvider).close();
    }

    @Test
    public void testCloseWithExceptionInCacheClose() throws Exception {
        doThrow(IOException.class).when(serviceCache).close();
        client.close();
        verify(serviceCache).close();
        verify(serviceProvider).close();
    }

    @Test
    public void testCloseWithExceptionInProviderClose() throws Exception {
        doThrow(IOException.class).when(serviceProvider).close();
        client.close();
        verify(serviceCache).close();
        verify(serviceProvider).close();
    }
}
