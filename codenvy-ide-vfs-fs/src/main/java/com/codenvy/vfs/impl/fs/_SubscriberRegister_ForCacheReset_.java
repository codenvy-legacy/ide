/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2013] Codenvy, S.A.
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.vfs.impl.fs;

import com.codenvy.api.core.notification.EventService;
import com.codenvy.api.core.notification.EventSubscriber;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Set;

/**
 * @author Sergii Leschenko
 */
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