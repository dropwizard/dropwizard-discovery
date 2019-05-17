package io.dropwizard.discovery.testutil;

import org.apache.curator.test.TestingServer;

import io.dropwizard.discovery.DiscoveryFactory;

public class TestDiscoveryFactory extends DiscoveryFactory {
    private final TestingServer testingServer;

    public TestDiscoveryFactory(TestingServer testingServer, String serviceName) {
        this.setServiceName(serviceName);
        this.testingServer = testingServer;
    }

    @Override
    public String getQuorumSpec() {
        return testingServer.getConnectString();
    }

}