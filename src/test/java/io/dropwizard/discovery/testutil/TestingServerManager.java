package io.dropwizard.discovery.testutil;

import org.apache.curator.test.TestingServer;

import io.dropwizard.lifecycle.Managed;

public class TestingServerManager implements Managed {

    private final TestingServer testingServer;

    public TestingServerManager(TestingServer testingServer) {
        this.testingServer = testingServer;
    }

    @Override
    public void start() throws Exception {
        this.testingServer.start();
    }

    @Override
    public void stop() throws Exception {
        this.testingServer.stop();
    }

}