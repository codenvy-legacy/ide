package com.codenvy.ide.ext.helloworld.server.inject;

import com.codenvy.ide.ext.helloworld.server.HelloWorldService;
import com.codenvy.ide.ext.helloworld.server.MyDependency;
import com.codenvy.inject.DynaModule;
import com.google.inject.AbstractModule;

/**
 * Extend AbstractModule, override its configure method and create new bindings.
 * @DynaModule annotation is necessary for auto-deploy of components to a Guice container
 */

@DynaModule
public class MyModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(HelloWorldService.class); // required, otherwise everrest framework won't recognize it
        bind(MyDependency.class); // may be omitted, class has simple constructor
    }
}
