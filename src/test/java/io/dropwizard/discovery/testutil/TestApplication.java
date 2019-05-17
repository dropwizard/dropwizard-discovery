package io.dropwizard.discovery.testutil;

import org.apache.curator.test.TestingServer;

import io.dropwizard.Application;
import io.dropwizard.discovery.DiscoveryBundle;
import io.dropwizard.discovery.DiscoveryFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class TestApplication extends Application<TestConfiguration> {
    private TestingServer testingServer;

    public TestApplication() {
        try {
            this.testingServer = new TestingServer();
        } catch (Exception e) {
            throw new IllegalStateException("Could not initialize testing server", e);
        }
    }

    private final DiscoveryBundle<TestConfiguration> discoveryBundle = new DiscoveryBundle<TestConfiguration>() {
        @Override
        public DiscoveryFactory getDiscoveryFactory(TestConfiguration configuration) {
            return new TestDiscoveryFactory(testingServer, configuration.getServiceName());
        }

        @Override
        public void run(TestConfiguration configuration, Environment environment) throws Exception {
            // Managed resources stop in reverse-order of how they started
            // We want to stop the testing server after the DiscoveryBundle stops.
            environment.lifecycle().manage(new TestingServerManager(testingServer));
            super.run(configuration, environment);
        }
    };

    @Override
    public void initialize(Bootstrap<TestConfiguration> bootstrap) {
        bootstrap.addBundle(discoveryBundle);
    }

    @Override
    public void run(TestConfiguration configuration, Environment environment) throws Exception {
        // use the configuration to pass this application's testing server to the test
        configuration.setTestingServer(testingServer);
    }
}