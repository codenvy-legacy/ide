package com.codenvy.vfs.impl.fs;

import com.codenvy.api.core.notification.EventService;
import com.codenvy.api.core.notification.EventSubscriber;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;
import com.codenvy.api.vfs.server.observation.UpdateACLEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;

@Singleton
public class _SubscriberRegister_ForCacheReset_ {
    private static final Logger LOG = LoggerFactory.getLogger(_IdeOldCacheUpdater.class);

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
            String wsId = event.getWorkspaceId();
            String path = event.getPath();
            String absolutePath = null;
            try {
                absolutePath = localFSMountStrategy.getMountPath(wsId) + path;
            } catch (VirtualFileSystemException e) {
                LOG.warn("Can not get path to workspace {}", wsId);
            }
            java.io.File cacheResetDir = new java.io.File(absolutePath, FSMountPoint.SERVICE_DIR + java.io.File.separatorChar + "cache");
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

    private final EventService eventService;

    private final Set<EventSubscriber> listeners;

    @Inject
    public _SubscriberRegister_ForCacheReset_(EventService eventService, Set<EventSubscriber> listeners) {
        this.eventService = eventService;
        this.listeners = listeners;
    }

    @PostConstruct
    @Inject
    public void subscribe() {
        for (EventSubscriber eventSubscriber : listeners) {
            eventService.subscribe(eventSubscriber);
        }
    }

    @PreDestroy
    @Inject
    public void unsubscribe(_IdeOldCacheUpdater cacheUpdater) {
        for (EventSubscriber eventSubscriber : listeners) {
            eventService.unsubscribe(eventSubscriber);
        }
    }
}