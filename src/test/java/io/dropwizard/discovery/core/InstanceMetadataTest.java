package io.dropwizard.discovery.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;

import org.junit.Test;

public class InstanceMetadataTest {

    private final UUID INSTANCE_ID = UUID.randomUUID();
    private final String LISTEN_ADDRESS = "0.0.0.0";
    private final int LISTEN_PORT = 1234;
    private final Integer ADMIN_PORT = 5678;

    private final InstanceMetadata metadata = new InstanceMetadata(INSTANCE_ID, LISTEN_ADDRESS, LISTEN_PORT,
            Optional.of(ADMIN_PORT));

    @Test
    public void testGetInstance() {
        assertThat(metadata.getInstanceId()).isEqualTo(INSTANCE_ID);
    }

    @Test
    public void testGetListenAddress() {
        assertThat(metadata.getListenAddress()).isEqualTo(LISTEN_ADDRESS);
    }

    @Test
    public void testGetListenPort() {
        assertThat(metadata.getListenPort()).isEqualTo(LISTEN_PORT);
    }

    @Test
    public void testGetAdminPort() {
        assertThat(metadata.getAdminPort().isPresent()).isTrue();
        assertThat(metadata.getAdminPort().get()).isEqualTo(ADMIN_PORT);
    }

    @Test
    public void testGetAdminPortNull() {
        InstanceMetadata metadataAdminPortNull = new InstanceMetadata(INSTANCE_ID, LISTEN_ADDRESS, LISTEN_PORT, null);
        assertThat(metadataAdminPortNull.getAdminPort().isPresent()).isFalse();
    }

    @Test
    public void testGetAdminPortNotPresent() {
        InstanceMetadata metadataAdminPortNotPresent = new InstanceMetadata(INSTANCE_ID, LISTEN_ADDRESS, LISTEN_PORT,
                Optional.empty());
        assertThat(metadataAdminPortNotPresent.getAdminPort().isPresent()).isFalse();
    }

    @Test
    public void testEquals() {
        assertThat(metadata).isEqualTo(metadata);
        assertThat(metadata).isNotEqualTo(null);

        InstanceMetadata equal = new InstanceMetadata(INSTANCE_ID, LISTEN_ADDRESS, LISTEN_PORT,
                Optional.of(ADMIN_PORT));
        assertThat(metadata).isEqualTo(equal);

        InstanceMetadata differentUUID = new InstanceMetadata(UUID.randomUUID(), LISTEN_ADDRESS, LISTEN_PORT,
                Optional.of(ADMIN_PORT));
        assertThat(metadata).isNotEqualTo(differentUUID);

        InstanceMetadata differentListenAddress = new InstanceMetadata(INSTANCE_ID, "127.0.0.1", LISTEN_PORT,
                Optional.of(ADMIN_PORT));
        assertThat(metadata).isNotEqualTo(differentListenAddress);

        InstanceMetadata differentListenPort = new InstanceMetadata(INSTANCE_ID, LISTEN_ADDRESS, 7890,
                Optional.of(ADMIN_PORT));
        assertThat(metadata).isNotEqualTo(differentListenPort);

        InstanceMetadata differentAdminPort = new InstanceMetadata(INSTANCE_ID, LISTEN_ADDRESS, LISTEN_PORT,
                Optional.of(7890));
        assertThat(metadata).isNotEqualTo(differentAdminPort);

        InstanceMetadata nullAdminPort = new InstanceMetadata(INSTANCE_ID, LISTEN_ADDRESS, LISTEN_PORT, null);
        assertThat(metadata).isNotEqualTo(nullAdminPort);

        InstanceMetadata missingAdminPort = new InstanceMetadata(INSTANCE_ID, LISTEN_ADDRESS, LISTEN_PORT,
                Optional.empty());
        assertThat(metadata).isNotEqualTo(missingAdminPort);
    }

    @Test
    public void testHashCode() {
        assertThat(metadata.hashCode()).isEqualTo(metadata.hashCode());
        InstanceMetadata equal = new InstanceMetadata(INSTANCE_ID, LISTEN_ADDRESS, LISTEN_PORT,
                Optional.of(ADMIN_PORT));
        assertThat(metadata.hashCode()).isEqualTo(equal.hashCode());
    }

    @Test
    public void testToString() {
        StringBuilder expectedString = new StringBuilder("InstanceMetadata{")
                .append("instanceId=").append(INSTANCE_ID.toString()).append(", ")
                .append("listenAddress=").append(LISTEN_ADDRESS).append(", ")
                .append("listenPort=").append(LISTEN_PORT).append(", ")
                .append("adminPort=").append(ADMIN_PORT).append("}");
        assertThat(metadata.toString()).isEqualTo(expectedString.toString());
    }
}