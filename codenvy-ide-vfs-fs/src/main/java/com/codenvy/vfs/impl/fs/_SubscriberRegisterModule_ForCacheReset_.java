package com.codenvy.vfs.impl.fs;

import com.codenvy.inject.DynaModule;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

@DynaModule
public class _SubscriberRegisterModule_ForCacheReset_ extends AbstractModule {

    @Override
    protected void configure() {
        Multibinder<com.codenvy.api.core.notification.EventSubscriber> subscriptionServiceBinder =
                Multibinder.newSetBinder(binder(), com.codenvy.api.core.notification.EventSubscriber.class);
        subscriptionServiceBinder.addBinding().to(_SubscriberRegister_ForCacheReset_._IdeOldCacheUpdater.class);

        bind(_SubscriberRegister_ForCacheReset_.class);
    }
}
