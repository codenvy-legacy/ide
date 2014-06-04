/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.vfs.impl.fs;

import com.codenvy.api.core.notification.EventService;
import com.codenvy.api.core.notification.EventSubscriber;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;
import com.codenvy.api.vfs.server.observation.UpdateACLEvent;
import com.codenvy.inject.DynaModule;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/** @author Sergii Leschenko */
@DynaModule
public class _IdeOldCacheUpdaterRegister_ForCacheReset_ extends AbstractModule {
    private static final Logger LOG = LoggerFactory.getLogger(_IdeOldCacheUpdater.class);

    @Override
    protected void configure() {
        Multibinder<com.codenvy.api.core.notification.EventSubscriber> subscriptionServiceBinder =
                Multibinder.newSetBinder(binder(), com.codenvy.api.core.notification.EventSubscriber.class);
        subscriptionServiceBinder.addBinding().to(_IdeOldCacheUpdater.class);

        bind(_SubscriberRegister_ForCacheReset_.class);
    }

    @Singleton
    public static class _SubscriberRegister_ForCacheReset_ {
        private final EventService eventService;

        private final Set<EventSubscriber> listeners;

        @Inject
        public _SubscriberRegister_ForCacheReset_(EventService eventService, _IdeOldCacheUpdater cacheUpdater) {
            this.eventService = eventService;
            listeners = new HashSet<EventSubscriber>(Arrays.asList(cacheUpdater));
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

    // This class will send signal for cache update in ide2.
    // When ide2 doesn't use own implementation of MountPoint then this listener will be redundant
    public static class _IdeOldCacheUpdater implements EventSubscriber<UpdateACLEvent> {
        private LocalFSMountStrategy localFSMountStrategy;

        @Inject
        public _IdeOldCacheUpdater(LocalFSMountStrategy localFSMountStrategy) {
            this.localFSMountStrategy = localFSMountStrategy;
        }

        @Override
        public void onEvent(UpdateACLEvent event) {
            String pathToVFSRoot;
            try {
                pathToVFSRoot = localFSMountStrategy.getMountPath(event.getWorkspaceId()) + event.getPath();
            } catch (VirtualFileSystemException e) {
                LOG.warn("Can not get path to workspace {} for cache update in ide2", event.getWorkspaceId());
                return;
            }

            java.io.File cacheResetDir = new java.io.File(pathToVFSRoot, FSMountPoint.SERVICE_DIR + java.io.File.separatorChar + "cache");
            if (!(cacheResetDir.exists() || cacheResetDir.mkdirs())) {
                LOG.warn("Unable to create folder {} for cache update in ide2", cacheResetDir.getPath());
            } else {
                java.nio.file.Path resetFilePath = new java.io.File(cacheResetDir, "reset_ide_old").toPath();
                if (!Files.exists(resetFilePath)) {
                    try {
                        Files.createFile(resetFilePath);
                    } catch (IOException e) {
                        LOG.warn("Unable to create file {} for cache update in ide2", resetFilePath.toAbsolutePath());
                    }
                }
            }
        }
    }
}
