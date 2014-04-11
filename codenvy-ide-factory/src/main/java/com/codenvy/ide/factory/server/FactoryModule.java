package com.codenvy.ide.factory.server;

import com.codenvy.api.factory.FactoryUrlValidator;
import com.codenvy.factory.FactoryUrlBaseValidator;
import com.codenvy.inject.DynaModule;
import com.google.inject.AbstractModule;

/**
 * @author Vladyslav Zhukovskii
 */
@DynaModule
public class FactoryModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(FactoryService.class);

        bind(com.codenvy.api.factory.FactoryService.class);
        bind(FactoryUrlValidator.class).to(FactoryUrlBaseValidator.class);
    }
}
