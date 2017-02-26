package io.dropwizard.discovery.core;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.annotation.Nonnull;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.dropwizard.lifecycle.ServerLifecycleListener;

public class CuratorAdvertisementListener<T>
        implements ServerLifecycleListener {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(CuratorAdvertisementListener.class);
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
        final int listenPort = getLocalPort(server);
        final int adminPort = getAdminPort(server);

        try {
            advertiser.initListenInfo(listenPort, adminPort);
            advertiser.registerAvailability();
        } catch (final Exception e) {
            LOGGER.error("Unable to register service in ZK", e);
        }
    }
}
