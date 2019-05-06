package io.dropwizard.discovery;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.test.TestingServer;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.InstanceSerializer;
import org.junit.ClassRule;
import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;

import io.dropwizard.discovery.core.InstanceMetadata;
import io.dropwizard.discovery.core.JacksonInstanceSerializer;
import io.dropwizard.discovery.testutil.TestApplication;
import io.dropwizard.discovery.testutil.TestConfiguration;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;

public class DiscoveryBundleIntegrationTest {
    private static final String NAMESPACE = "dropwizard";
    private static final String SERVICE_NAME = "test-service";
    private static final String SERVICE_PATH = "/service/" + SERVICE_NAME;

    @ClassRule
    public static final DropwizardAppRule<TestConfiguration> RULE = new DropwizardAppRule<TestConfiguration>(
            TestApplication.class, ResourceHelpers.resourceFilePath("test-config.yaml"));

    InstanceSerializer<InstanceMetadata> serializer = new JacksonInstanceSerializer<InstanceMetadata>(
            RULE.getObjectMapper(), new TypeReference<ServiceInstance<InstanceMetadata>>() {
            });

    private String instancePath(String instanceNode) {
        return new StringBuilder(SERVICE_PATH).append("/").append(instanceNode).toString();
    }

    @Test
    public void testRegistration() throws Exception {
        // use a separate instance of curator to pull out the app's data
        TestingServer testingServer = RULE.getConfiguration().getTestingServer();
        try (final CuratorFramework curatorFramework = CuratorFrameworkFactory
                .newClient(testingServer.getConnectString(), new RetryOneTime(1))) {
            curatorFramework.start();

            List<String> instanceNames = curatorFramework.usingNamespace(NAMESPACE).getChildren().forPath(SERVICE_PATH);
            assertThat(instanceNames).hasSize(1);

            String instanceName = instanceNames.get(0);
            byte[] instanceData = curatorFramework.usingNamespace(NAMESPACE).getData()
                    .forPath(instancePath(instanceName));

            ServiceInstance<InstanceMetadata> serviceInstanceMetadata = serializer.deserialize(instanceData);

            assertThat(serviceInstanceMetadata).isNotNull();
            assertThat(serviceInstanceMetadata.getName()).isEqualTo(SERVICE_NAME);
        }
    }
}