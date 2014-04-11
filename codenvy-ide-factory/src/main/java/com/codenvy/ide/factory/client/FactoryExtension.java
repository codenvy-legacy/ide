package com.codenvy.ide.factory.client;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.factory.client.accept.AcceptFactoryHandler;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Vladyslav Zhukovskii
 */
@Singleton
@Extension(title = "Factory", version = "3.0.0")
public class FactoryExtension {

    @Inject
    public FactoryExtension(AcceptFactoryHandler acceptFactoryHandler) {
        //Entry point to start up factory acceptance
        acceptFactoryHandler.processFactory();
    }
}
