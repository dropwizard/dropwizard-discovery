package io.dropwizard.discovery.core;

import org.apache.curator.x.discovery.ServiceInstance;

public class DefaultServiceInstanceFactory implements
        ServiceInstanceFactory<InstanceMetadata> {

    @Override
    public ServiceInstance<InstanceMetadata> build(String serviceName,
            CuratorAdvertiser<InstanceMetadata> advertiser) throws Exception {
        final InstanceMetadata metadata = new InstanceMetadata(
                advertiser.getInstanceId(), advertiser.getListenAddress(),
                advertiser.getListenPort(), advertiser.getAdminPort());
        return ServiceInstance.<InstanceMetadata> builder().name(serviceName)
                .address(advertiser.getListenAddress())
                .port(advertiser.getListenPort())
                .id(advertiser.getInstanceId().toString()).payload(metadata)
                .build();
    }

    @Override
    public Class<InstanceMetadata> getPayloadClass() {
        return InstanceMetadata.class;
    }

}
