Dropwizard Discovery
====================
[![Build Status](https://travis-ci.org/dropwizard/dropwizard-discovery.svg?branch=master)](https://travis-ci.org/dropwizard/dropwizard-discovery)
[![Coverage Status](https://coveralls.io/repos/dropwizard/dropwizard-discovery/badge.svg?branch=master)](https://coveralls.io/r/dropwizard/dropwizard-discovery?branch=master)
[![Maven Central](https://img.shields.io/maven-central/v/io.dropwizard.modules/dropwizard-discovery.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/io.dropwizard.modules/dropwizard-discovery/)

`dropwizard-discovery` is a [Dropwizard](http://dropwizard.io) [bundle](http://dropwizard.io/manual/core.html#bundles) that can be used to register a Dropwizard service into [Zookeeper](https://zookeeper.apache.org) upon startup. Connectivity to Zookeeper is provided by Netflix's [Curator](http://curator.apache.org) library and its built in [Service Discovery](http://curator.apache.org/curator-x-discovery/index.html) framework. This code was originally open sourced by General Electric as [snowizard-discovery](https://github.com/GeneralElectric/snowizard-discovery) under a BSD license.


Usage
-----

In your Dropwizard [Configuration](http://dropwizard.io/manual/core.html#configuration) file, add a property to represent the discovery configuration for your service:

```yaml
# Discovery-related settings.
discovery:
    serviceName: hello-world
```

And have your configuration class expose the `DiscoveryFactory`:

```java
public class HelloWorldConfiguration extends Configuration {

    @Valid
    @NotNull
    private DiscoveryFactory discovery = new DiscoveryFactory();

    @JsonProperty("discovery")
    public DiscoveryFactory getDiscoveryFactory() {
        return discovery;
    }

    @JsonProperty("discovery")
    public void setDiscoveryFactory(DiscoveryFactory discoveryFactory) {
        this.discovery = discoveryFactory;
    }
}
```

If you only wish to have your service register itself with Zookeeper and you don't intend on consuming any other services, you just need to add the following into the [`Application#initialize`](http://dropwizard.io/0.7.1/dropwizard-core/apidocs/io/dropwizard/Application.html#initialize(io.dropwizard.setup.Bootstrap)) method:

```java
public class HelloWorldApplication extends Application<HelloWorldConfiguration> {

    private final DiscoveryBundle<HelloWorldConfiguration> discoveryBundle = new DiscoveryBundle<HelloWorldConfiguration>(){

            @Override
            public DiscoveryFactory getDiscoveryFactory(HelloWorldConfiguration configuration) {
                return configuration.getDiscoveryFactory();
            }

        };

    @Override
    public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {
        bootstrap.addBundle(discoveryBundle);
    }
}
```

where `HelloWorldConfiguration` is your configuration class name.

If you want to also consume other services, you can store an instance of the `DiscoveryBundle` so that you can retrieve a new `DiscoveryClient` to access additional services.

```java
public class HelloWorldApplication extends Application<HelloWorldConfiguration> {

    private final DiscoveryBundle<HelloWorldConfiguration> discoveryBundle = new DiscoveryBundle<HelloWorldConfiguration>() {
        @Override
        public DiscoveryFactory getDiscoveryFactory(HelloWorldConfiguration configuration) {
            return configuration.getDiscoveryFactory();
        }
    };

    @Override
    public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {
        bootstrap.addBundle(discoveryBundle);
    }

    @Override
    public void run(HelloWorldConfiguration configuration, Environment environment) throws Exception {
        final DiscoveryClient client = discoveryBundle.newDiscoveryClient("other-service");
        environment.lifecycle().manage(new DiscoveryClientManager(client));
    }
}
```

Be sure to register the `DiscoveryClient` using the `DiscoveryClientManager` as a [Managed Object](http://dropwizard.io/manual/core.html#managed-objects) so that it is properly started and shutdown when your service is stopped and started.


Maven Artifacts
---------------

This project is available on Maven Central. To add it to your project simply add the following dependencies to your `pom.xml`:

```xml
<dependency>
  <groupId>io.dropwizard.modules</groupId>
  <artifactId>dropwizard-discovery</artifactId>
  <version>0.9.1-1</version>
</dependency>
```

Enhancements
------------

1. Support an "[Advertise Locally, Lookup Globally](http://whilefalse.blogspot.com/2012/12/building-global-highly-available.html)" model that [Camille Fournier](https://github.com/skamille) outlined on her blog by supporting separate Zookeeper connections, one that connects locally and one that connects to a global instance.


Support
-------

Please file bug reports and feature requests in [GitHub issues](https://github.com/dropwizard/dropwizard-discovery/issues).


License
-------

Copyright (c) 2015 Justin Plock

This library is licensed under the Apache License, Version 2.0.

See http://www.apache.org/licenses/LICENSE-2.0.html or the LICENSE file in this repository for the full license text.
