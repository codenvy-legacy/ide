package com.codenvy.vfs.impl.fs;

import com.codenvy.api.core.notification.EventService;
import com.codenvy.api.core.notification.EventSubscriber;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Set;

@Singleton
public class _SubscriberRegister_ForCacheReset_ {
    private final EventService eventService;

    private final Set<EventSubscriber> listeners;

    @Inject
    public _SubscriberRegister_ForCacheReset_(EventService eventService, Set<EventSubscriber> listeners) {
        this.eventService = eventService;
        this.listeners = listeners;
    }

    @PostConstruct
    public void subscribe() {
        for (EventSubscriber eventSubscriber : listeners) {
            eventService.subscribe(eventSubscriber);
        }
    }

    @PreDestroy
    public void unsubscribe() {
        for (EventSubscriber eventSubscriber : listeners) {
            eventService.unsubscribe(eventSubscriber);
        }
    }
}