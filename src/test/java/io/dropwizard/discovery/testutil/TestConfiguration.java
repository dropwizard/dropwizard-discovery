package io.dropwizard.discovery.testutil;

import org.apache.curator.test.TestingServer;
import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;

public class TestConfiguration extends Configuration {
    @NotBlank
    private String serviceName = "test-service";

    /**
     * This is set by TestingApplication during DiscoveryBundle initialization.
     * <p>
     * It is retrieved in tests through DropwizardAppRule or
     * DropwizardTestingSupport.
     **/
    private TestingServer testingServer = null;

    @JsonProperty
    public String getServiceName() {
        return this.serviceName;
    }

    @JsonProperty
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public TestingServer getTestingServer() {
        return testingServer;
    }

    public void setTestingServer(TestingServer testingServer) {
        this.testingServer = testingServer;
    }
}