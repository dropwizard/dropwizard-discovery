package io.dropwizard.discovery.core;

import static com.google.common.base.Preconditions.checkNotNull;
import io.dropwizard.lifecycle.ServerLifecycleListener;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import javax.annotation.Nonnull;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CuratorAdvertisementListener<T> implements ServerLifecycleListener {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(CuratorAdvertisementListener.class);
    private static final String APPLICATION_CONNECTOR = "application";
    private static final String ADMIN_CONNECTOR = "admin";
    private final CuratorAdvertiser<T> advertiser;

    /**
     * Constructor
     * 
     * @param advertiser
     *            {@link CuratorAdvertiser}
     */
    public CuratorAdvertisementListener(
            @Nonnull final CuratorAdvertiser<T> advertiser) {
        this.advertiser = checkNotNull(advertiser);
    }

    @Override
    public void serverStarted(final Server server) {
        // Detect the port Jetty is listening on - works with configured and
        // random ports
        int listenPort = 0;
        Integer adminPort = null;
        for (final Connector connector : server.getConnectors()) {
            try {
                final ServerSocketChannel channel = (ServerSocketChannel) connector
                        .getTransport();
                final InetSocketAddress socket = (InetSocketAddress) channel
                        .getLocalAddress();

                switch (connector.getName()) {
                case APPLICATION_CONNECTOR:
                    listenPort = socket.getPort();
                    break;
                case ADMIN_CONNECTOR:
                    adminPort = socket.getPort();
                    break;
                }
            } catch (Exception e) {
                LOGGER.error(
                        "Unable to get port from connector: "
                                + connector.getName(), e);
            }
        }

        try {
            advertiser.initListenInfo(listenPort, adminPort);
            advertiser.registerAvailability();
        } catch (final Exception e) {
            LOGGER.error("Unable to register service in ZK", e);
        }
    }
}
