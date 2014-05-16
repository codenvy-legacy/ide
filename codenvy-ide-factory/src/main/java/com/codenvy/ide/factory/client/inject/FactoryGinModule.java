package com.codenvy.ide.factory.client.inject;

import com.codenvy.api.factory.gwt.client.FactoryServiceClient;
import com.codenvy.api.factory.gwt.client.FactoryServiceClientImpl;
import com.codenvy.ide.api.extension.ExtensionGinModule;
import com.codenvy.ide.factory.client.accept.AcceptFactoryHandler;
import com.codenvy.ide.factory.client.share.ShareFactoryView;
import com.codenvy.ide.factory.client.share.ShareFactoryViewImpl;
import com.codenvy.ide.factory.client.welcome.GreetingPart;
import com.codenvy.ide.factory.client.welcome.GreetingPartPresenter;
import com.codenvy.ide.factory.client.welcome.GreetingPartView;
import com.codenvy.ide.factory.client.welcome.GreetingPartViewImpl;
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
        bind(FactoryServiceClient.class).to(FactoryServiceClientImpl.class).in(Singleton.class);
        
        bind(ShareFactoryView.class).to(ShareFactoryViewImpl.class).in(Singleton.class);
        bind(GreetingPart.class).to(GreetingPartPresenter.class).in(Singleton.class);
        bind(GreetingPartView.class).to(GreetingPartViewImpl.class).in(Singleton.class);
    }
}
