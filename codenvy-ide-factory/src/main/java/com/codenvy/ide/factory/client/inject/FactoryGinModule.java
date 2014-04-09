package com.codenvy.ide.factory.client.inject;

import com.codenvy.ide.api.extension.ExtensionGinModule;
import com.codenvy.ide.factory.client.FactoryClientService;
import com.codenvy.ide.factory.client.FactoryClientServiceImpl;
import com.codenvy.ide.factory.client.accept.AcceptFactoryHandler;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

/**
 * @author Vladyslav Zhukovskii
 */
@ExtensionGinModule
public class FactoryGinModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(AcceptFactoryHandler.class).in(Singleton.class);
        bind(FactoryClientService.class).to(FactoryClientServiceImpl.class).in(Singleton.class);
    }
}
